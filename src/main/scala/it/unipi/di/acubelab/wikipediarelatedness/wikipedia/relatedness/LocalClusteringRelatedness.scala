package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.options.LocalClusteringOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.WikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.{ClusteringCoefficient, WikiBVGraph}

/**
  * rel(u, v) = 1 / | \intersection(in(u), in(v)) |  *  \sum_{ w \in \intersection(in(u), in(v)) } \frac{ local_clust_out(w) }
 *
  * @param
  *       {
  *           graph: inGraph/outGraph   // graph where compute the clustering coefficient
  *       }
  */
class LocalClusteringRelatedness(options: LocalClusteringOptions) extends Relatedness {
  val neighborGraph = WikiGraph.wikiBVGraph(options.neighborGraph)

  val clusterGraph = WikiGraph.wikiBVGraph(options.clusterGraph)
  val clustCoeff = new ClusteringCoefficient(clusterGraph)


  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Double = {
    val intersection = neighborGraph.nodeIntersection(srcWikiID, dstWikiID)
    if (intersection.size == 0) return 0.0

    val numerator = localClusterNumerator(intersection)

    val relatedness = numerator / intersection.size.toDouble
    println("Coeff: %1.2f with numerator %1.2f".format(relatedness, numerator))

    relatedness
  }

  def localClusterNumerator(wikiIDs: IntArrayList) : Double = {
    var localClusterSum = 0.0

    for(i <- 0 until wikiIDs.size) {
      localClusterSum += clustCoeff.localClusteringCoefficient(wikiIDs.getInt(i))
    }

    localClusterSum
  }

  override def toString() : String = {
    "LocalClusteringRelatedness"
  }
}
