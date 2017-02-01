package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.similarity
import java.lang.Double

import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms.ppr.StandardBasisPrior
import org.apache.commons.collections15.Transformer
import org.slf4j.{Logger, LoggerFactory}


class PPRRanker(iterations: Int, pprDecay: Double) extends PriorRanker(iterations, pprDecay) {
  override protected def logger: Logger = LoggerFactory.getLogger(getClass)
  var executedIterations = 0

  /**
    * Prior vector of WikiID.
    *
    * @param wikiID
    * @return
    */
  override protected def getPrior(wikiID: Int): Transformer[Int, Double] = new StandardBasisPrior(wikiID)


  // sistemare queste, override nothhy
  override protected var simScore: Double = _


  /**
    * Updates the similiarity score after an iteration of computeSimilarity
    *
    * @param srcRanks
    * @param dstRanks
    */
  override protected def updateSimilarityScore(srcRanks: Seq[(Int, Double)], dstRanks: Seq[(Int, Double)]): Unit = {
    executedIterations += 1
    if (executedIterations == iterations) {


      val floatSrc = srcRanks.map { case ((index, score)) => (index, score.toFloat) }
      val floatDst = dstRanks.map { case ((index, score)) => (index, score.toFloat) }
      simScore = Similarity.cosineSimilarity(floatSrc, floatDst).toDouble
    }
  }
}
