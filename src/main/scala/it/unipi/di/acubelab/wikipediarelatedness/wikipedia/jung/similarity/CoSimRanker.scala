package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.similarity
import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import org.slf4j.LoggerFactory



class CoSimRanker(iterations: Int, pprAlpha: Double, val csrDecay: Double) extends PPRRanker(iterations , pprAlpha) {
  override protected val logger = LoggerFactory.getLogger(getClass)


  /**
    * Updates the similiarity score after an iteration of computeSimilarity
    *
    * @param srcRanks
    * @param dstRanks
    * @param iteration current iteration number
    */
  override protected def updateSimilarityScore(srcRanks: Seq[(Int, Double)], dstRanks: Seq[(Int, Double)],
                                                  iteration: Int): Unit = {

    val floatSrc = srcRanks.map { case ((index, s)) => (index, s.toFloat) }
    val floatDst = dstRanks.map { case ((index, s)) => (index, s.toFloat) }

    score += Math.pow(csrDecay, iteration) * Similarity.cosineSimilarity(floatSrc, floatDst).toDouble
  }


  override def getSimilarityScore : Double = (1 - csrDecay) * score
}