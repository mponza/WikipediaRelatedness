package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.similarity

import it.unipi.di.acubelab.webgraph.PPRVectors
import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import org.slf4j.LoggerFactory

class WikiBVCoSimRank(iterations: Int, alpha: Float, val decay: Float) extends WikiBVPPR(iterations, alpha){
  protected val logger = LoggerFactory.getLogger(getClass)


  protected def similarity(srcPPRVectors: PPRVectors, dstPPRVectors: PPRVectors) = {
    var score = 0.0
    for(i <- 0 until iterations) {
      val srcPPRs = srcPPRVectors.pprs(i)
      val dstPPRs = dstPPRVectors.pprs(i)

      score += Math.pow(decay, i) * Similarity.cosineSimilarity( srcPPRs, dstPPRs )
    }

    (1 - decay) * score.toFloat
  }
}