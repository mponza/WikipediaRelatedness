package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.cosimrank

import it.unimi.dsi.fastutil.doubles.DoubleArrayList
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.law.rank.{PageRank, PageRankPowerSeries}
import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.{WikiGraph, WikiGraphFactory}
import org.slf4j.LoggerFactory

class CoSimRankPowerSeries(wikiGraph: WikiGraph = WikiGraphFactory.outGraph, iterations: Int = 30,
                           pprDecay: Float = 0.8f, csrDecay: Float = 0.8f)

  extends CoSimRank(wikiGraph, iterations, pprDecay, csrDecay) {

  override def getLogger() = LoggerFactory.getLogger(classOf[CoSimRankPowerSeries])

  override def similarity(srcWikiID: Int, dstWikiID: Int) : Float = {
    val pprVectors = List(srcWikiID, dstWikiID).par.map(computePPRVectors(_))
    // Parallel PPR vector mapping
    val srcPPRvectors = pprVectors(0)
    val dstPPRvectors = pprVectors(1)

    var csrSimilarity = 0.0
    for(i <- 0 until iterations) {
      val srcPPRvector = srcPPRvectors.get(i)
      val dstPPRvector = dstPPRvectors.get(i)

      csrSimilarity += math.pow(csrDecay, i) * Similarity.cosineSimilarity(srcPPRvector, dstPPRvector)
    }

    println("*************")
    println(csrSimilarity.toFloat)
    csrSimilarity.toFloat
    System.exit(1)
    csrSimilarity.toFloat

  }

  override def getPageRanker() : PageRank = {
    logger.info("Initializing PageRankPowerSeries...")

    val pageRanker = new PageRankPowerSeries(wikiGraph.graph)
    pageRanker.alpha = pprDecay.toDouble

    pageRanker
  }

  override def computePPRVectors(wikiID: Int): ObjectArrayList[DoubleArrayList] = {
    val ranker = getPageRanker()  // allows parallel computations (.par.map(...))

    // Vector of 0.0 with 1.0 in wikiID.
    ranker.preference = preferenceVector(wikiID)

    val pprVectors = new ObjectArrayList[DoubleArrayList]()

    ranker.init()
    for (i <- 0 until iterations) {
      ranker.step()

      val pprVector = new DoubleArrayList(ranker.rank)
      pprVectors.add(pprVector)

    }
    pprVectors
  }
}