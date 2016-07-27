package it.unipi.di.acubelab.graphrel.wikipedia.relatedness

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unipi.di.acubelab.graphrel.dataset.WikiRelTask
import it.unipi.di.acubelab.graphrel.wikipedia.WikiGraph

class LocalClusteringRelatedness(options: Map[String, Any]) extends Relatedness {
  val graphName = if (options.contains("graph")) options("graph").toString else "symGraph"
  val graph = WikiGraph.wikiBVGraph(graphName)


  def computeRelatedness(wikiRelTask: WikiRelTask) : Double = {
    val srcWikiID = wikiRelTask.src.wikiID
    val dstWikiID = wikiRelTask.dst.wikiID

    val intersection = graph.nodeIntersection(srcWikiID, dstWikiID)
    val numerator = localClusterNumerator(intersection)

    val sizeA = graph.outdegree(srcWikiID)
    val sizeB = graph.outdegree(dstWikiID)

    numerator / (sizeA + sizeB - intersection.size)
  }

  def localClusterNumerator(wikiIDs: IntArrayList) : Double = {
    var localClusterSum = 0.0

    for(i <- 0 until wikiIDs.size) {
      localClusterSum += graph.localClusteringCoefficient(wikiIDs.getInt(i))
    }

    localClusterSum
  }
}
