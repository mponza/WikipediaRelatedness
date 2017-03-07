package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.similarity

import it.unimi.dsi.law.rank.SpectralRanking.IterationNumberStoppingCriterion
import it.unipi.di.acubelab.webgraph.{PPRParallelGaussSeidel, PPRVectors}


/**
  * Abstract class for the computation of the stationary distribution over the whole Wikipedia.
  * @param iterations
  * @param alpha
  */
abstract class WikiBVPPR(val iterations: Int, val alpha: Float) {

  def similarity(srcWikiID: Int, dstWikiID: Int): Float = {
    if (srcWikiID == dstWikiID) return 1f


    val srcPPRVectors = computePPRVectors(srcWikiID)
    val dstPPRVectors = computePPRVectors(dstWikiID)

    similarity(srcPPRVectors, dstPPRVectors)
  }


  protected def computePPRVectors(wikiID: Int) = {
    val pageRank = new PPRParallelGaussSeidel()
    pageRank.alpha = alpha

    val pprVectors = new PPRVectors
    pageRank.stepUntil(wikiID, new IterationNumberStoppingCriterion(iterations), pprVectors)

    pprVectors
  }


  /**
    * Similarity between two set of PPRVectors of ranking distribution biased respectively to srcWikiID and dstWikiID.
    *
    * @param srcPPRVectors
    * @param dstPPRVectors
    */
  protected def similarity(srcPPRVectors: PPRVectors, dstPPRVectors: PPRVectors) : Float

}