package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.similarity

import it.unimi.dsi.fastutil.doubles.DoubleArrayList
import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.ranker.GaussSeidelPageRanker
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory

class WebGraphCoSimRank(val iterations: Int, val alpha: Float, val decay: Float) {
  protected val logger = LoggerFactory.getLogger(getClass)
  val wikiBVGraph = WikiBVGraphFactory.make("in")


  def similarity(srcWikiID: Int, dstWikiID: Int): Float = {
    if (srcWikiID == dstWikiID) return 1f

    val srcRanker = new GaussSeidelPageRanker(srcWikiID, wikiBVGraph, alpha)
    val dstRanker = new GaussSeidelPageRanker(dstWikiID, wikiBVGraph, alpha)

    var score = 0D
    for(i <- 1 to iterations) {

      val srcPPR = srcRanker.rankingStep()
      val dstPPR = dstRanker.rankingStep()

      score += Math.pow(decay, i) * Similarity.cosineSimilarity(new DoubleArrayList(srcPPR), new DoubleArrayList(dstPPR))
    }

    score.toFloat
  }
}