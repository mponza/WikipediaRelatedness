package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.pagerank

import it.unimi.dsi.fastutil.doubles.{DoubleArrayList, DoubleList}
import it.unimi.dsi.law.rank.PageRank
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.{WikiGraph, WikiGraphFactory}
import org.slf4j.Logger

abstract class PersonalizedPageRank(val wikiGraph: WikiGraph = WikiGraphFactory.outGraph,
                                      val iterations: Int = 30, val pprDecay: Float = 0.8f) {
    protected val logger = getLogger()
    protected val pageRanker = getPageRanker()

    def getLogger() : Logger

    def getPageRanker(): PageRank

    def dotProduct(src: DoubleArrayList, dst: DoubleArrayList) : Double = {
      if (src.size() != dst.size()) throw new IllegalArgumentException("Dot product error. Lists have different size.")
      var dot = 0.0

      for(i <- 0 until src.size()) {
        dot += src.getDouble(i) * dst.getDouble(i)
      }

      dot
    }

    /**
      * Builds the preference vector of a given WikipediaID.
      */
    def preferenceVector(wikiID: Int) : DoubleList = {
      val preference = Array.fill[Double](wikiGraph.graph.numNodes())(0.0)

      val nodeID = wikiGraph.getNodeID(wikiID)
      preference(nodeID) = 1.0

      new DoubleArrayList(preference)
    }

}
