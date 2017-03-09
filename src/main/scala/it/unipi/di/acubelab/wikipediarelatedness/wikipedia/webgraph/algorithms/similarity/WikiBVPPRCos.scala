package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.similarity

import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import org.slf4j.LoggerFactory

class WikiBVPPRCos(iterations: Int, alpha: Float) extends WikiBVPPR(iterations, alpha){
  protected val logger = LoggerFactory.getLogger(getClass)


  override protected def similarity(srcPPRVectors: List[Seq[(Int, Float)]], dstPPRVectors: List[Seq[(Int, Float)]]) : Float = {
    val srcPPRs = srcPPRVectors.last
    val dstPPRs = dstPPRVectors.last

    Similarity.cosineSimilarity( srcPPRs, dstPPRs )
  }
}