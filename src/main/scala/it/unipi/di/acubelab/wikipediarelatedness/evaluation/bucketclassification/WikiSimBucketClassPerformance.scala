package it.unipi.di.acubelab.wikipediarelatedness.evaluation.bucketclassification

import java.lang.Double
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.WikiSimPerformance

class WikiSimBucketClassPerformance(scores: Map[String, Map[String, Double]]) extends WikiSimPerformance {

  override def toString: String = {
    "Low (%d) - %s\nMedium (%d) - %s\nHigh (%d) - %s\nAVG_F1: %1.2f\n".format(
      scores("low")("size").toInt, bucetkScoreToString(scores("low")),
      scores("medium")("size").toInt, bucetkScoreToString(scores("medium")),
      scores("high")("size").toInt, bucetkScoreToString(scores("high")),
      avgF1()
    )
  }

  def avgF1() : scala.Double = {
    // Scores computed upon at least one element.
    val noEmptyScores = scores.filter {
      case (key, values) => scores(key)("size") > 0.0
    }

    noEmptyScores.keys.map(scores(_)("f1").toDouble).sum / scores.size.toDouble
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
         "High_Precision", "High_Recall", "High_F1",
         "AVG_F1"
    )
  }

  def csvValues() : List[scala.Double] = {
    List(scores("low")("precision"), scores("low")("recall"), scores("low")("f1"),
         scores("medium")("precision"), scores("medium")("recall"), scores("medium")("f1"),
         scores("high")("precision"), scores("high")("recall"), scores("high")("f1"),
         avgF1()
    )
  }
}
