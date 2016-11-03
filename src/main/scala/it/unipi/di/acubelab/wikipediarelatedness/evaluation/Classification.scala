package it.unipi.di.acubelab.wikipediarelatedness.evaluation

import edu.stanford.nlp.stats.PrecisionRecallStats
import org.slf4j.LoggerFactory

class Classification {}

object Classification {
  val logger = LoggerFactory.getLogger(classOf[Classification])


  def precRecF1(predictions: List[Double], groundTruth: List[Double]) : List[Float] = {
    if (predictions.length != groundTruth.length) {
      throw new IllegalArgumentException("Prediction and groundtruth array have the be the same size.")
    }

    // Counts true/false positives/negatives.
    val stats = new PrecisionRecallStats()
    for((pred, truth) <- predictions.zip(groundTruth)) {
      (pred, truth) match {
        case (1.0, 1.0) => stats.incrementTP()
        case (1.0, 0.0) => stats.incrementFP()
        case (0.0, 1.0) => stats.incrementFN()
        case (0.0, 0.0) => ;
      }
    }

    List(stats.getPrecision.toFloat, stats.getRecall.toFloat, stats.getFMeasure.toFloat)
  }

}
