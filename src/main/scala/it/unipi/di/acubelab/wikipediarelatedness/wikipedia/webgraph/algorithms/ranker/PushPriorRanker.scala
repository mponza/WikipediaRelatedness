package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.ranker

import it.unimi.dsi.law.rank.PageRankPush
import it.unimi.dsi.law.rank.PageRankPush.L1NormStoppingCritertion
import it.unimi.dsi.law.rank.SpectralRanking.IterationNumberStoppingCriterion
import it.unimi.dsi.webgraph.Transform
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraph
import org.slf4j.LoggerFactory


/**
  * Uses is carefully.
  *
  * @param wikiID
  * @param wikiBVGraph
  * @param alpha
  */
class PushPriorRanker(val wikiID: Int, val wikiBVGraph: WikiBVGraph, val alpha: Float) extends WebGraphPageRanker {
  protected val logger = LoggerFactory.getLogger(getClass)

  protected val ppr = new PageRankPush( Transform.filterArcs(wikiBVGraph.graph, Transform.NO_LOOPS) , true)
  ppr.alpha = alpha
  ppr.root = wikiBVGraph.wiki2node.get(wikiID)
  ppr.backToRoot = 1.0
  ppr.init()


  /**
    * Performs one step of PageRank and returns the ranking vector.
    *
    * @return
    */
  def rankingStep(): Array[Double] = {
    ppr.stepUntil(new IterationNumberStoppingCriterion(1))

    // Scala code adapted from http://law.di.unimi.it/software/law-docs/it/unimi/dsi/law/rank/PageRankPush.html
    val rank = Array.ofDim[Double]( ppr.graph.numNodes() )
    for(i <- ppr.node2Seen.size() until 0 by -1 ) {
      rank( ppr.seen2Node(i) ) = ppr.rank(i) / ppr.pNorm
      println( ppr.rank(i) )
    }

    rank
  }


  def rankingSteps(iterations: Int) = {
    ppr.stepUntil(new L1NormStoppingCritertion())

    // Scala code adapted from http://law.di.unimi.it/software/law-docs/it/unimi/dsi/law/rank/PageRankPush.html
    val rank = Array.ofDim[Double]( ppr.graph.numNodes() )
    for(i <- ppr.node2Seen.size() until 0 by -1 ) {
      rank( ppr.seen2Node(i) ) = ppr.rank(i) / ppr.pNorm
      println( ppr.rank(i) )
    }

    rank
  }

}
