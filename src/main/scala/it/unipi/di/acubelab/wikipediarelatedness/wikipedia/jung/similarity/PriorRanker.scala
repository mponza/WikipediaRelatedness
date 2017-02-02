package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.similarity


import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph.WikiJungGraph
import org.apache.commons.collections15.Transformer
import org.slf4j.Logger

import scala.collection.mutable.ListBuffer


/**
  * Computes a similiarity score by performing several iterations on a graph.
  *
  * @param iterations
  * @param pprDecay
  */
abstract class PriorRanker(val iterations: Int, val pprDecay: Double) extends SimRanker {

  protected def logger: Logger
  protected var score: Double    // similarity score


  /**
    * Computes similarity by running PageRankWithPriors on srcWikiID and dstWikiID.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @param wikiJungGraph
    * @return
    */
  override def similarity(srcWikiID: Int, dstWikiID: Int, wikiJungGraph: WikiJungGraph): Double = {
    val srcPPR = pageRanker( wikiJungGraph, getPrior(srcWikiID) )
    val dstPPR = pageRanker( wikiJungGraph, getPrior(dstWikiID) )

    initSimilarityScore()
    computeSimilarity(wikiJungGraph, srcPPR, dstPPR)
    getSimilarityScore
  }


  /**
    * Returns PageRankWithPriors configurated over the directed and weighted wikiJungGraph biased with prior.
    *
    */
  protected def pageRanker(wikiJungGraph: WikiJungGraph, prior: Transformer[Int, java.lang.Double]) :
    PageRankWithPriors[Int, Long] = {

    val ppr = new PageRankWithPriors[Int, Long](wikiJungGraph.graph, wikiJungGraph.weights, prior, pprDecay)

    ppr.setMaxIterations(iterations)
    ppr.setTolerance(0.0)

    ppr
  }


  /**
    * Computes the similarity between two PageRankWithPriors.
    *
    * @param srcPPR
    * @param dstPPR
    * @return
    */
  protected def computeSimilarity(wikiJungGraph: WikiJungGraph,
                                    srcPPR: PageRankWithPriors[Int, Long], dstPPR: PageRankWithPriors[Int, Long]) = {
    for (i <- 0 until iterations) {
      srcPPR.step()
      dstPPR.step()

      val srcRanks = getRanks(wikiJungGraph, srcPPR)
      val dstRanks = getRanks(wikiJungGraph, dstPPR)

      updateSimilarityScore(srcRanks, dstRanks, i)
    }
  }


  /**
    * Returns a vector with the PageRank ranks.
    *
    * @param pageRankWithPriors
    */
  protected def getRanks(wikiJungGraph: WikiJungGraph, pageRankWithPriors: PageRankWithPriors[Int, Long]) :
    Seq[(Int, Double)] = {
    import scala.collection.JavaConversions._

      val ranks = ListBuffer.empty[Tuple2[Int, Double]]

      for (wikiID <- wikiJungGraph.graph.getVertices) {
        val pprScore = pageRankWithPriors.getVertexScore(wikiID)

        if (pprScore.isNaN) {
          ranks += Tuple2(wikiID, 0f)
        } else {
          ranks += Tuple2(wikiID, pprScore)
        }
      }

     ranks.sortBy(_._1)
  }


  /**
    * Prior vector of WikiID.
    *
    * @param wikiID
    * @return
    */
  protected def getPrior(wikiID: Int): Transformer[Int, java.lang.Double]


  /**
    * Initializes the similarity score before the similarity computation.
    */
  protected def initSimilarityScore()


  /**
    * Updates the similiarity score after an iteration of computeSimilarity
    *
    * @param srcRanks
    * @param dstRanks
    * @param iteration 0-based
    */
  protected def updateSimilarityScore(srcRanks: Seq[(Int, Double)], dstRanks: Seq[(Int, Double)], iteration: Int) : Unit



  /**
    * Returns the final similarity score after the similarity computation
    *
    * @return
    */
  def getSimilarityScore : Double = score

}
