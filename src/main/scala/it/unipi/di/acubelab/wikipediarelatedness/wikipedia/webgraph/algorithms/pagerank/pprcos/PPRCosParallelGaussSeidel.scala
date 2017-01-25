package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.pagerank.pprcos

import it.unimi.dsi.fastutil.doubles.DoubleArrayList
import it.unimi.dsi.law.rank.{PageRank, PageRankParallelGaussSeidel, SpectralRanking}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.{WikiGraph, WikiGraphFactory}
import org.slf4j.LoggerFactory

class PPRCosParallelGaussSeidel(wikiGraph: WikiGraph = WikiGraphFactory.inGraph, iterations: Int = 100,
                                pprDecay: Float = 0.8f)
  extends PPRCos(wikiGraph, iterations, pprDecay) {


  override def getLogger() = LoggerFactory.getLogger(classOf[PPRCosParallelGaussSeidel])


  override def getPageRanker() : PageRank = {
    logger.info("Initializing PageRankParallelGaussSeidel ...")

    val pageRanker = new PageRankParallelGaussSeidel(wikiGraph.graph)  // inGraph, transform not needed.
    pageRanker.alpha = pprDecay.toDouble

    pageRanker
  }


  override def computePPRVector(wikiID: Int): DoubleArrayList = {
    // Vector of 0.0 with 1.0 in wikiID.
    pageRanker.preference = preferenceVector(wikiID)
    pageRanker.stepUntil(new SpectralRanking.IterationNumberStoppingCriterion(iterations))

    new DoubleArrayList(pageRanker.rank)
  }

}