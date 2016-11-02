package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.pagerank.cosimrank

import it.unimi.dsi.fastutil.doubles.DoubleArrayList
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.law.rank.{PageRank, PageRankParallelGaussSeidel, SpectralRanking}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.{WikiGraph, WikiGraphFactory}
import org.slf4j.LoggerFactory

/**
  *
  * @param wikiGraph Wikipedia Graph, stored via WebGraph. It have to be transposed!
  * @param iterations  number of PPR vectors by CoSimRank
  * @param pprDecay    PageRank weight decay (d in the paper)
  * @param csrDecay    CoSimRank weight decay (c in the paper)
  *
  */
class CoSimRankParallelGaussSeidel(wikiGraph: WikiGraph = WikiGraphFactory.inGraph, iterations: Int = 30,
                                   pprDecay: Float = 0.8f, csrDecay: Float = 0.8f)

  extends CoSimRank(wikiGraph, iterations, pprDecay, csrDecay) {


  override def getLogger() = LoggerFactory.getLogger(classOf[CoSimRankParallelGaussSeidel])

  override def getPageRanker() : PageRank = {
    logger.info("Initializing PageRankParallelGaussSeidel ...")

    val pageRanker = new PageRankParallelGaussSeidel(wikiGraph.graph)
    pageRanker.alpha = pprDecay.toDouble

    pageRanker
  }

  override def computePPRVectors(wikiID: Int): ObjectArrayList[DoubleArrayList] = {
    // Vector of 0.0 with 1.0 in wikiID.
    pageRanker.preference = preferenceVector(wikiID)

    val pprVectors = new ObjectArrayList[DoubleArrayList]()

    // At each PPR iteration we save the PPR distribution.
    for (i <- 0 until iterations) {
      pageRanker.stepUntil(new SpectralRanking.IterationNumberStoppingCriterion(i))

      val pprVector = new DoubleArrayList(pageRanker.rank)
      pprVectors.add(pprVector)

    }
    pprVectors
  }
}
