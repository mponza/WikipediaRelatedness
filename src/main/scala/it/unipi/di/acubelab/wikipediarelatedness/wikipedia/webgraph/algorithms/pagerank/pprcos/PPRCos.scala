package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.pagerank.pprcos

import it.unimi.dsi.fastutil.doubles.DoubleArrayList
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.law.rank.{PageRank, PageRankPowerSeries}
import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.pagerank.PersonalizedPageRank
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.{WikiBVGraph, WikiBVGraphFactory}
import org.slf4j.LoggerFactory

abstract class PPRCos (wikiGraph: WikiBVGraph = WikiBVGraphFactory.outWikiBVGraph, iterations: Int = 30, pprDecay: Float = 0.8f)

  extends PersonalizedPageRank(wikiGraph, iterations, pprDecay) {

  def getLogger() = LoggerFactory.getLogger(classOf[PersonalizedPageRank])


  /**
    * Computes CoSimRank similarity score between two Wikipedia nodes.
    *
    * @param srcWikiID
    * @param dstWikiID
    */
  def similarity(srcWikiID: Int, dstWikiID: Int) : Float = {
    val srcPPRvector = computePPRVector(srcWikiID)
    val dstPPRvector = computePPRVector(dstWikiID)

    val pprSimilarity = Similarity.cosineSimilarity(srcPPRvector, dstPPRvector)

    pprSimilarity.toFloat
  }


  override def getPageRanker() : PageRank = {
    logger.info("Initializing PageRankPowerSeries...")

    val pageRanker = new PageRankPowerSeries(wikiGraph.graph)
    pageRanker.alpha = pprDecay.toDouble

    pageRanker
  }

  /**
    * Runs PersonalizedPageRank on the Wikipedia graph by drugging the preference vector of wikiID.
    *
    * @return List of PPRVectors, where the i-th vector is the PPR distribution at i-th iteration.
    */
  def computePPRVector(wikiID: Int): DoubleArrayList = {
    // Vector of 0.0 with 1.0 in wikiID.
    pageRanker.preference = preferenceVector(wikiID)

    pageRanker.init()
    for (i <- 0 until iterations) {
      pageRanker.step()
    }

    new DoubleArrayList(pageRanker.rank)
  }

}
