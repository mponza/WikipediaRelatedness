package it.unipi.di.acubelab.graphrel.evaluation.classification

import java.lang.Double
import scala.language.existentials

import com.google.common.collect.Range
import it.unipi.di.acubelab.graphrel.dataset.WikiRelTask
import it.unipi.di.acubelab.graphrel.evaluation.WikiSimEvaluator
import it.unipi.di.acubelab.graphrel.evaluation.WikiSimPerformance

class WikiSimClassificator(val tasks: List[WikiRelTask]) extends WikiSimEvaluator {

  def scores = classificationScores()

  /**
    * @return {"low/medium/high" -> {"F1/Precision/Recall" -> Double}}
    * */
  def classificationScores() : Map[String, Map[String, Double]] = {
    val buckets = Map("low" -> Range.closed(new Double(0.0), new Double(0.3)),
                      "medium" -> Range.openClosed(new Double(0.3), new Double(0.7)),
                      "high" -> Range.openClosed(new Double(0.7), new Double(1.0)))

    buckets.map {
      case (bucketName, range) =>
        val labels = bucketizeLabelRange(tasks, range)
        bucketName -> Map(
            "precision" -> precision(labels),
            "recall" -> recall(labels),
            "f1" -> f1(labels)
        )
    }
  }

  def bucketizeLabelRange(tasks: List[WikiRelTask], range: Range[Double]) : List[(Int, Int)]= {
    tasks.map {
      task =>
        val humanLabel = if (range.contains(task.rel)) 1 else 0
        val computedLabel = if (range.contains(task.computedRel)) 1 else 0

        (humanLabel, computedLabel)
    }
  }

  def truePositive(labelPairs: List[(Int, Int)]) : Int = {
    labelPairs.foldLeft(0)((tp, pair) => if (pair._1 == pair._2 && pair._1 == 1) tp + 1 else tp)
  }

  def trueNegative(labelPairs: List[(Int, Int)]) : Int = {
    labelPairs.foldLeft(0)((tn, pair) => if (pair._1 == pair._2 && pair._1 == 0) tn + 1 else tn)
  }

  def falsePositive(labelPairs: List[(Int, Int)]) : Int = {
    labelPairs.foldLeft(0)((fp, pair) => if (pair._1 == pair._2 && pair._1 == 1) fp else fp + 1)
  }

  def falseNegative(labelPairs: List[(Int, Int)]) : Int = {
    labelPairs.foldLeft(0)((fn, pair) => if (pair._1 == pair._2 && pair._1 == 0) fn else fn + 1)
  }

  def precision(labels: List[(Int, Int)]) : Double = {
    truePositive(labels) / (math.max(truePositive(labels) + trueNegative(labels), 1.0))
  }

  def recall(labels: List[(Int, Int)]) : Double = {
    truePositive(labels) / (math.max(truePositive(labels) + falseNegative(labels), 1.0))
  }

  def f1(labels: List[(Int, Int)]) : Double = {
    2 * precision(labels) * recall(labels) / (math.max(precision(labels) + recall(labels), 1.0))
  }

  override def wikiSimPerformance() : WikiSimPerformance = {
    new WikiSimClassPerformance(scores)
  }

  override def toString() = "classification"
}
