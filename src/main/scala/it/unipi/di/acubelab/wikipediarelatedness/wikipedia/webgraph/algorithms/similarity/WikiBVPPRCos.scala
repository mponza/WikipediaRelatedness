package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.similarity

import it.unipi.di.acubelab.webgraph.PPRVectors
import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import org.slf4j.LoggerFactory

class WikiBVPPRCos(iterations: Int, alpha: Float, val decay: Float) extends WikiBVPPR(iterations, alpha){
  protected val logger = LoggerFactory.getLogger(getClass)


  protected def similarity(srcPPRVectors: PPRVectors, dstPPRVectors: PPRVectors) = {
    val srcPPRs = srcPPRVectors.pprs.last
    val dstPPRs = dstPPRVectors.pprs.last

    Similarity.cosineSimilarity( srcPPRs, dstPPRs )
  }
}