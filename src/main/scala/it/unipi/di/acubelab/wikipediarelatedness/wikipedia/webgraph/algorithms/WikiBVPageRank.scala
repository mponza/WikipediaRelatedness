package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms

import it.unimi.dsi.law.rank.SpectralRanking.{IterationNumberStoppingCriterion, NormStoppingCriterion}
import it.unimi.dsi.law.rank.PageRankParallelGaussSeidel
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping.WikiTitleID
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory


/**
  * Classical PageRank computation on the Wikipedia graph.
  *
  */
class WikiBVPageRank {

  protected val logger = LoggerFactory.getLogger(getClass)

  protected val wikiGraph = WikiBVGraphFactory.make("in")
  protected val ranks = computePageRanks()
  protected val posRanks = computePositionPageRank()

  def getPageRank(wikiID: Int) = ranks( wikiGraph.getNodeID( wikiID ) )

  def getPositionPageRank(wikiID: Int) = posRanks( wikiGraph.getNodeID( wikiID ) )


  protected def computePageRanks() = {
    logger.info("Computing PageRank...")
    val ranker =  new PageRankParallelGaussSeidel(wikiGraph.graph)
    ranker.stepUntil(new IterationNumberStoppingCriterion(5))
    //ranker.stepUntil(new NormStoppingCriterion(0.001))
    ranker.rank
  }


  /**
    * Computes array of WikiID and their position.
    *
    * @return
    */
  protected def computePositionPageRank() = {
    // sorted list of WikiIDs by their non-increasing PageRank score
    val sortedRank = ranks.zipWithIndex.sortBy(_._1).reverse.map(_._2)

    // Array of WikiID: PageRank position
    val posRank = Array.ofDim[Int](wikiGraph.numNodes() + 1)
    sortedRank.zipWithIndex.foreach {
      case ((nodeID, index)) => println(nodeID)
        posRank(nodeID) = index
    }

    logger.info("Top PageRanked Wikipedia Page: %s".format( WikiTitleID.map( sortedRank.head )) )
    logger.info("Last PageRanked Wikipedia Page: %s".format( WikiTitleID.map( sortedRank.last )) )
    posRank
  }

}
