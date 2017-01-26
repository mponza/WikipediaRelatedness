package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.set

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import it.unipi.di.acubelab.wikipediarelatedness.options.JaccardTopOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.operations.SetOperations
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.triangles.LocalClustering
import org.slf4j.LoggerFactory

/**
  * Jaccard where union and intersection are computed by taking into account only top-scored nodes
  * according with the specified heuristics.
 *
  * @param options
  */
class JaccardTopRelatedness(val options: JaccardTopOptions) extends Relatedness  {
  val logger = LoggerFactory.getLogger(classOf[JaccardTopRelatedness])

  val wikiGraph = WikiBVGraphFactory.makeWikiBVGraph(options.graph)
  val setOperations = new SetOperations(wikiGraph)
  val lc = new LocalClustering()


  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {

    val srcTopK = topKSuccessors(srcWikiID)
    val dstTopK = topKSuccessors(dstWikiID)

    val intersection = srcTopK.intersect(dstTopK)
    if (intersection.length == 0) return 0f

    intersection.length / srcTopK.union(dstTopK).length.toFloat
  }


  def topKSuccessors(wikiID: Int) : Array[Int] = {
    val successors = wikiGraph.wikiSuccessors(wikiID)
    val sorted = successors.sortBy(node => lc.getCoefficient(wikiID)).reverse

    sorted.slice(0, options.threshold)
  }
}
