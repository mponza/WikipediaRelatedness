package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.similarity

import it.unimi.dsi.law.rank.SpectralRanking.IterationNumberStoppingCriterion
import it.unipi.di.acubelab.webgraph.{PPRParallelGaussSeidel, PPRTask}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory


/**
  * Abstract class for the computation of the stationary distribution over the whole Wikipedia.
  * Methods commonly used to be overrided:
  *
  *   - getPreferenceVector, if non-uniform is needed
  *   - similarity, by taking into account several stationary distributions.
  *
  * @param iterations
  * @param alpha
  */
abstract class WikiBVPPR(val iterations: Int, val alpha: Float) {
  protected val wikiBVgraph = WikiBVGraphFactory.make("in")

  def similarity(srcWikiID: Int, dstWikiID: Int): Float = {
    if (srcWikiID == dstWikiID) return 1f


    val srcPPRVectors = computePPRVectors(srcWikiID)
    val dstPPRVectors = computePPRVectors(dstWikiID)

    similarity(srcPPRVectors, dstPPRVectors)
  }


  protected def computePPRVectors(wikiID: Int) : List[Seq[(Int, Float)]]= {
    val pageRank = new PPRParallelGaussSeidel()
    pageRank.alpha = alpha

    val preference = getPreferenceVector(wikiID)
    val pprTask = new PPRTask(preference)

    pageRank.stepUntil(new IterationNumberStoppingCriterion(iterations), pprTask)

    pprTask.pprs.toList
  }


  /**
    * Default uniform preference vector.
    * @param wikiID
    * @return
    */
  protected def getPreferenceVector(wikiID: Int): Array[Double]= {
    val nodeID = wikiBVgraph.getNodeID(wikiID)
    val preference = Array.ofDim[Double]( wikiBVgraph.numNodes() )

    preference(nodeID) = 1.0

    preference
  }


  /**
    * Similarity between two set of PPRVectors of ranking distribution biased respectively to srcWikiID and dstWikiID.
    *
    * @param srcPPRVectors
    * @param dstPPRVectors
    */
  protected def similarity(srcPPRVectors: List[Seq[(Int, Float)]], dstPPRVectors: List[Seq[(Int, Float)]]) : Float

}