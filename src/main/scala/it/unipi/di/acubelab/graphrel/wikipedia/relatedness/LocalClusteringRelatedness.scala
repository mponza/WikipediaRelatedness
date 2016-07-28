package it.unipi.di.acubelab.graphrel.wikipedia.relatedness

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unipi.di.acubelab.graphrel.dataset.WikiRelTask
import it.unipi.di.acubelab.graphrel.wikipedia.WikiGraph
import it.unipi.di.acubelab.graphrel.wikipedia.processing.webgraph.{ClusteringCoefficient, WikiBVGraph}

/**
  * rel(u, v) = 1 / | \intersection(in(u), in(v)) |  *  \sum_{ w \in \intersection(in(u), in(v)) } \frac{ local_clust_out(w) }
  * @param
  *       {
  *           graph: inGraph/outGraph   // graph where compute the clustering coefficient
  *       }
  */
class LocalClusteringRelatedness(options: Map[String, Any]) extends Relatedness {
  val inGraph = WikiGraph.wikiBVGraph("inGraph")

  val graphName = options.getOrElse("graph", "outGraph").toString
  val clustGraph = WikiGraph.wikiBVGraph(graphName)
  val clustCoeff = new ClusteringCoefficient(clustGraph)


  def computeRelatedness(wikiRelTask: WikiRelTask) : Double = {
    val srcWikiID = wikiRelTask.src.wikiID
    val dstWikiID = wikiRelTask.dst.wikiID

    val intersection = inGraph.nodeIntersection(srcWikiID, dstWikiID)
    if (intersection.size == 0) return 0.0

    val numerator = localClusterNumerator(intersection)

    val x = numerator / intersection.size.toDouble
    println("Coeff: %1.2f with numerator %1.2f".format(x, numerator))

    x
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
