package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.similarity

import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors
import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph.WikiJungGraph
import org.apache.commons.collections15.Transformer
import org.slf4j.{Logger, LoggerFactory}


class PPRRanker(iterations: Int, pprAlpha: Double) extends PriorRanker(iterations, pprAlpha) {
  override protected def logger = LoggerFactory.getLogger(getClass)
  override protected var score = 0.0

  override protected def initSimilarityScore() = score = 0.0

  /**
    * Prior vector of WikiID is its canonical base.
    *
    * @param wikiID
    * @return
    */
  override protected def getPrior(wikiID: Int): Transformer[Int, java.lang.Double] = new StandardBasisPrior(wikiID)


  /**
    * Computes the similarity between two PageRankWithPriors.
    *
    * @param srcPPR
    * @param dstPPR
    * @return
    */
  protected override def computeSimilarity(wikiJungGraph: WikiJungGraph,
                                  srcPPR: PageRankWithPriors[Int, Long], dstPPR: PageRankWithPriors[Int, Long]) = {
    for (i <- 0 until iterations) {
      logger.info("%d iteration...")
      srcPPR.step()
      dstPPR.step()

      if (i == iterations - 1) {
        val srcRanks = getRanks(wikiJungGraph, srcPPR)
        val dstRanks = getRanks(wikiJungGraph, dstPPR)

        updateSimilarityScore(srcRanks, dstRanks, i)
      }
    }
  }

  /**
    * Updates the similiarity score after an iteration of computeSimilarity
    *
    * @param srcRanks
    * @param dstRanks
    */
  protected def updateSimilarityScore(srcRanks: Seq[(Int, scala.Double)], dstRanks: Seq[(Int, scala.Double)],
                                                  iteration: Int): Unit = {
    if (iteration == iterations - 1) {
      val floatSrc = srcRanks.map { case ((index, s)) => (index, s.toFloat) }
      val floatDst = dstRanks.map { case ((index, s)) => (index, s.toFloat) }
      score = Similarity.cosineSimilarity(floatSrc, floatDst).toDouble
    }
  }


}
