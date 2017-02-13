package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.ranker

import it.unimi.dsi.fastutil.doubles.DoubleArrayList
import it.unimi.dsi.law.rank.PageRankParallelGaussSeidel
import it.unimi.dsi.law.rank.SpectralRanking.IterationNumberStoppingCriterion
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraph
import org.slf4j.LoggerFactory


/**
  * wikiBVGraph is the transpose of the original graph (in).
  * @param wikiID
  * @param wikiBVGraph
  * @param alpha
  */
class GaussSeidelPageRanker(val wikiID: Int, val wikiBVGraph: WikiBVGraph, val alpha: Float) extends WebGraphPageRanker {
  protected val logger = LoggerFactory.getLogger(getClass)

  protected var prior = Array.ofDim[Double]( wikiBVGraph.graph.numNodes() )
  prior( wikiBVGraph.getNodeID(wikiID) ) = 1.0

  protected val ppr = new PageRankParallelGaussSeidel( wikiBVGraph.graph )
  ppr.alpha = alpha
  ppr.preference = new DoubleArrayList(prior)
  ppr.init()

  /**
    * Performs one step of PageRank and returns the ranking vector.
    *
    * @return
    */
  def rankingStep(): Array[Double] = {
    ppr.rank = prior

    ppr.stepUntil( new IterationNumberStoppingCriterion(1) ) //
    prior = ppr.rank

    ppr.rank
  }


  def rankingSteps(iterations: Int): Array[Double] = {
    ppr.stepUntil( new IterationNumberStoppingCriterion(iterations) )
    ppr.rank
  }

}
