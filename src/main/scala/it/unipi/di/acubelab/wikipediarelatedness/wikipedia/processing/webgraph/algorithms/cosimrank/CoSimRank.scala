package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.cosimrank

import it.unimi.dsi.fastutil.doubles.{DoubleArrayList, DoubleList}
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.law.rank.PageRank
import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.{WikiGraph, WikiGraphFactory}
import org.slf4j.Logger

/**
  * CoSimRank algorithm for the Wikipedia graph.
  *
  * @param wikiGraph Wikipedia Graph, stored via WebGraph.
  * @param iterations  number of PPR vectors by CoSimRank
  * @param pprDecay    PageRank weight decay (d in the paper)
  * @param csrDecay    CoSimRank weight decay (c in the paper)
  *
  */
abstract class CoSimRank(val wikiGraph: WikiGraph = WikiGraphFactory.outGraph, val iterations: Int = 30,
                           val pprDecay: Float = 0.8f, val csrDecay: Float = 0.8f) {
  protected val logger = getLogger()
  protected val pageRanker = getPageRanker()

  def getLogger() : Logger

  def getPageRanker(): PageRank

  /**
    * Computes CoSimRank similarity score between two Wikipedia nodes.
    *
    * @param srcWikiID
    * @param dstWikiID
    */
  def similarity(srcWikiID: Int, dstWikiID: Int) : Float = {
    val srcPPRvectors = computePPRVectors(srcWikiID)
    val dstPPRvectors = computePPRVectors(dstWikiID)

    var csrSimilarity = 0.0
    for(i <- 0 until iterations) {
      val srcPPRvector = srcPPRvectors.get(i)
      val dstPPRvector = dstPPRvectors.get(i)

      csrSimilarity += math.pow(csrDecay, i) * Similarity.cosineSimilarity(srcPPRvector, dstPPRvector)
    }

    csrSimilarity.toFloat
  }

  /**
    * Runs PersonalizedPageRank on the Wikipedia graph by drugging the preference vector of wikiID.
    *
    * @return List of PPRVectors, where the i-th vector is the PPR distribution at i-th iteration.
    */
  def computePPRVectors(wikiID: Int): ObjectArrayList[DoubleArrayList]

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
