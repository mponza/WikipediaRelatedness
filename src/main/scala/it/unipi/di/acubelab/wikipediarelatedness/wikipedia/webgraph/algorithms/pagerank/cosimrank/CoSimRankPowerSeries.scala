package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.pagerank.cosimrank

import it.unimi.dsi.fastutil.doubles.DoubleArrayList
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.law.rank.{PageRank, PageRankPowerSeries}
import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.{WikiGraph, WikiGraphFactory}
import org.slf4j.LoggerFactory

class CoSimRankPowerSeries(wikiGraph: WikiGraph = WikiGraphFactory.outGraph, iterations: Int = 30,
                           pprDecay: Float = 0.8f, csrDecay: Float = 0.8f)

  extends CoSimRank(wikiGraph, iterations, pprDecay, csrDecay) {

  override def getLogger() = LoggerFactory.getLogger(classOf[CoSimRankPowerSeries])

  override def getPageRanker() : PageRank = {
    logger.info("Initializing PageRankPowerSeries...")

    val pageRanker = new PageRankPowerSeries(wikiGraph.graph)

    pageRanker.alpha = pprDecay.toDouble

    pageRanker
  }

  override def computePPRVectors(wikiID: Int): ObjectArrayList[DoubleArrayList] = {
    // Vector of 0.0 with 1.0 in wikiID.
    pageRanker.preference = preferenceVector(wikiID)

    val pprVectors = new ObjectArrayList[DoubleArrayList]()

    pageRanker.init()
    for (i <- 0 until iterations) {
      pageRanker.step()

      val pprVector = new DoubleArrayList(pageRanker.rank)
      pprVectors.add(pprVector)

    }
    pprVectors
  }
}