package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.set

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.clustering.LocalClustering
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessOptions}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.SetOperations
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory

class LocalClusteringRelatedness(val options: RelatednessOptions) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[LocalClusteringRelatedness])

  val setOperations = new SetOperations(WikiBVGraphFactory.makeWikiBVGraph(options.graph))
  val lc = new LocalClustering()


  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    if (srcWikiID == dstWikiID) return 1f

    // Intersection and union weighted by local clustering coefficients.
    val intersection  = setOperations.wikiIntersection(srcWikiID, dstWikiID).toIntArray().map(lc.getCoefficient(_)).sum
    if (intersection == 0) return 0f

    val union = setOperations.wikiUnion(srcWikiID, dstWikiID).toIntArray().map(lc.getCoefficient(_)).sum

    intersection / union
  }


  override def toString(): String = "LocalClustering_%s".format(options)
}