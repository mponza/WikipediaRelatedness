package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms

import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms.utils.{JungEdgeWeights, JungPersonalizedPrior}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph.JungWikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.Logger

import scala.collection.mutable.ListBuffer

/**
  * Computes PageRank-based similarity scores between two nodes in junkWikiGraph by weighting edges with relatedness.
  *
  * An effective class has to implement PageRankVectors (function which compute the PPR vectors by running PPR) and
  * the similarity function.
  *
  * @param junkWikiGraph
  * @param relatedness
  * @param iterations
  * @param pprDecay
  */
abstract class JungPPRSimilarity(val junkWikiGraph: JungWikiGraph, val relatedness: Relatedness,
                                 val iterations: Int = 30, val pprDecay: Float = 0.8f) {

  def logger: Logger


  /**
    * Returns PPR
    *
    * @param wikiID
    * @return
    */
  protected def pageRanker(wikiID: Int) = {
    val weights = new JungEdgeWeights(relatedness, junkWikiGraph.graph)
    val prior = new JungPersonalizedPrior(wikiID)

    new PageRankWithPriors[Int, String](junkWikiGraph.graph, prior, pprDecay.toDouble)
  }


  /**
    * Returns a list of [(wikiID, PPRScore)].
    *
    * @param ranker
    * @return
    */
  protected def getRankingVector(ranker: PageRankWithPriors[Int, String]) = {
    import scala.collection.JavaConversions._

    val vector = ListBuffer.empty[Tuple2[Int, Float]]

    for (wikiID <- junkWikiGraph.graph.getVertices) {
      vector += Tuple2(wikiID, ranker.getVertexScore(wikiID).toFloat)
    }

    vector.toList
  }


  /**
    * Compute PPR Vectors of wikiID
    *
    * @param wikiID
    * @return
    */
  protected def pageRankVectors(wikiID: Int): List[List[Tuple2[Int, Float]]]


  def similarity(srcWikiID: Int, dstWikiID: Int): Float

}
