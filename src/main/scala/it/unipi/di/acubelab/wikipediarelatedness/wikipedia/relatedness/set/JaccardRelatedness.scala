package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.set

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessOptions}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.SetOperations


class JaccardRelatedness(val options: RelatednessOptions) extends Relatedness {
  val wikiGraph = WikiBVGraphFactory.makeWikiBVGraph(options.graph)
  val setOperations = new SetOperations(wikiGraph)

  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    val intersection = setOperations.intersectionSize(srcWikiID, dstWikiID)
    if (intersection == 0) return 0f

    val sizeA = wikiGraph.outdegree(srcWikiID)
    val sizeB = wikiGraph.outdegree(dstWikiID)

    intersection / (sizeA + sizeB - intersection).toFloat
  }

  override def toString () : String = { "Jaccard_graph:%s".format(options.graph) }
}
