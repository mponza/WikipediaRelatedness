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

  def csvFields() : List[String] = {
    List("Low_Precision", "Low_Recall", "Low_F1",
         "Medium_Precision", "Medium_Recall", "Medium_F1",
         "High_Precision", "High_Recall", "High_F1"
    )
  }

  def csvValues() : List[scala.Double] = {
    List(scores("low")("precision"), scores("low")("recall"), scores("low")("f1"),
         scores("medium")("precision"), scores("medium")("recall"), scores("medium")("f1"),
         scores("high")("precision"), scores("high")("recall"), scores("high")("f1")
    )
  }
}
