package it.unipi.di.acubelab.graphrel.evaluation.classification

import java.lang.Double
import it.unipi.di.acubelab.graphrel.evaluation.WikiSimPerformance

class WikiSimClassPerformance(scores: Map[String, Map[String, Double]]) extends WikiSimPerformance {

  override def toString: String = {
    "Low - %s\nMedium - %s\nHigh - %s".format(
      bucetkScoreToString(scores("low")),
      bucetkScoreToString(scores("medium")),
      bucetkScoreToString(scores("high"))
    )
  }

  def bucetkScoreToString(bucketScore: Map[String, Double]) : String = {
    "Precision: %1.2f, Recall: %1.2f, F1: %1.2f".format(
      bucketScore("precision").toDouble,
      bucketScore("recall").toDouble,
      bucketScore("f1").toDouble
    )
  }
}
