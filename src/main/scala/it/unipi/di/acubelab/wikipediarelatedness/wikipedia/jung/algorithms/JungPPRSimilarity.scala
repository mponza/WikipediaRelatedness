package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms
/*
import edu.uci.ics.jung.algorithms.scoring.util.ScoringUtils
import edu.uci.ics.jung.algorithms.scoring.{PageRank, PageRankWithPriors}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms.utils.JungPersonalizedPrior
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms.utils.weighting.JungEdgeWeights
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.oldgraph.JungWikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.Logger

import scala.collection.mutable.ListBuffer

/**
  * Computes PageRank-based similarity scores between two nodes in junkWikiGraph by weighting edges with relatedness.
  *
  * An effective class has to implement PageRankVectors (function which compute the PPR vectors by running PPR) and
  * the similarity function.
  *
  * WARNING PPR Side effect: junkWikiGraph is cleaned from edges with relatedenss 0.
  *
  * @param junkWikiGraph
  * @param relatedness
  * @param iterations
  * @param pprDecay
  */
abstract class JungPPRSimilarity(val junkWikiGraph: JungWikiGraph, val relatedness: Relatedness,
                                 val iterations: Int = 30, val pprDecay: Float = 0.8f) {
  def logger: Logger

  val weights = new JungEdgeWeights(relatedness, junkWikiGraph)


  /**
    * Returns PPR
    *
    * @param wikiID
    * @return
    */
  protected def pageRanker(wikiID: Int) = {
    val prior = new JungPersonalizedPrior(wikiID)
    val pr = new PageRankWithPriors[Int, String](junkWikiGraph.graph, weights, prior, pprDecay)
    //pr.setTolerance(0.0)
    pr.setMaxIterations(iterations)
    pr.setTolerance(0.0)

    pr
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
      val pprScore = ranker.getVertexScore(wikiID).toFloat

      if (pprScore.isNaN) {
        vector += Tuple2(wikiID, 0f)
      } else {
        vector += Tuple2(wikiID, pprScore)
      }
    }

    vector.toList
  }


  /**
    * Compute PPR Vectors of wikiID.
    *
    * @param wikiID
    * @return
    */
  protected def pageRankVectors(wikiID: Int): List[List[Tuple2[Int, Float]]]


  def similarity(srcWikiID: Int, dstWikiID: Int): Float

}
*/