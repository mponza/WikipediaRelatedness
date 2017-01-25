package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.pagerank.cosimrank

import it.unimi.dsi.fastutil.doubles.{DoubleArrayList, DoubleList}
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.law.rank.PageRank
import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.pagerank.PersonalizedPageRank
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.{WikiGraph, WikiGraphFactory}
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
abstract class CoSimRank(wikiGraph: WikiGraph = WikiGraphFactory.outGraph, iterations: Int = 30,
                           pprDecay: Float = 0.8f, val csrDecay: Float = 0.8f)

           extends PersonalizedPageRank(wikiGraph, iterations, pprDecay) {

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

    (1 - csrDecay) * csrSimilarity.toFloat
  }

  /**
    * Runs PersonalizedPageRank on the Wikipedia graph by drugging the preference vector of wikiID.
    *
    * @return List of PPRVectors, where the i-th vector is the PPR distribution at i-th iteration.
    */
  def computePPRVectors(wikiID: Int): ObjectArrayList[DoubleArrayList]

}
