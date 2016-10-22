package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.options.LocalClusteringOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.ClusteringCoefficient
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.{WikiGraph$, WikiGraphFactory}

/**
  * rel(u, v) = 1 / | \intersection(in(u), in(v)) |  *  \sum_{ w \in \intersection(in(u), in(v)) } \frac{ local_clust_out(w) }
 *
  * @param
  *       {
  *           graph: inGraph/outGraph   // graph where compute the clustering coefficient
  *       }
  */
class LocalClusteringRelatedness(options: LocalClusteringOptions) extends Relatedness {
  val neighborGraph = WikiGraphFactory.wikiBVGraph(options.neighborGraph)

  val clusterGraph = WikiGraphFactory.wikiBVGraph(options.clusterGraph)
  val clustCoeff = new ClusteringCoefficient(clusterGraph)


  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    val intersection = neighborGraph.nodeIntersection(srcWikiID, dstWikiID)
    if (intersection.size == 0) return 0.0f

    val numerator = localClusterNumerator(intersection)

    val relatedness = numerator / intersection.size

    relatedness
  }

  def localClusterNumerator(wikiIDs: IntArrayList) : Float = {
    var localClusterSum = 0.0f

    for(i <- 0 until wikiIDs.size) {
      localClusterSum += clustCoeff.localClusteringCoefficient(wikiIDs.getInt(i))
    }

    localClusterSum
  }

  override def toString() : String = {
    "LocalClusteringRelatedness"
  }
}
