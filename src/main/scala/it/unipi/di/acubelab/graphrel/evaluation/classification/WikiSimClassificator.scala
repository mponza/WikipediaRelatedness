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

    val bucketLabels = tasks.map{
      task =>
          val relLabel = value2Label(task.rel, buckets)
          val computedLabel = value2Label(task.computedRel, buckets)

          (relLabel, computedLabel)
    }

    buckets.map {
      case (labelClass, range) =>
        // Intersection between different classes is not null.
        // E.g. labelClass == low, pair == (real: medium, predicted: low)
        // This pair is a false positive for low, but a false negative for medium.
        val labels = bucketLabels.filter(pair => pair._1 == labelClass || pair._2 == labelClass)

        labelClass -> Map(
            "precision" -> precision(labels, labelClass),
            "recall" -> recall(labels, labelClass),
            "f1" -> f1(labels, labelClass),
            "size" -> new Double(labels.size)
        )
    }
  }

  /**
    *  @return Label ("low", "medium", "high") of a relatedness value.
    */
  def value2Label(value: Double, buckets: Map[String, Range[Double]]) : String = {
    buckets.filter {
      case (name, range) =>
        range.contains(value)
    }.head._1
  }

  def truePositive(labelPairs: List[(String, String)], labelClass: String) : Int = {
    labelPairs.foldLeft(0)((tp, pair) => if (pair._1 == pair._2 && pair._1 == labelClass) tp + 1 else tp)
  }

  def trueNegative(labelPairs: List[(String, String)], labelClass: String) : Int = {
    labelPairs.foldLeft(0)((tn, pair) => if (pair._1 == pair._2 && pair._1 != labelClass) tn + 1 else tn)
  }

  def falsePositive(labelPairs: List[(String, String)], labelClass: String) : Int = {
    labelPairs.foldLeft(0)((fp, pair) => if (pair._1 != pair._2 && pair._2 == labelClass) fp + 1 else fp)
  }

  def falseNegative(labelPairs: List[(String, String)], labelClass: String) : Int = {
    labelPairs.foldLeft(0)((fn, pair) => if (pair._1 != pair._2 && pair._1 == labelClass) fn + 1 else fn)
  }

  def precision(labels: List[(String, String)], labelClass: String) : Double = {
    truePositive(labels, labelClass) /
      math.max(truePositive(labels, labelClass) + falsePositive(labels, labelClass), 1.0)
  }

  def recall(labels: List[(String, String)], labelClass: String) : Double = {
    truePositive(labels, labelClass) /
      math.max(truePositive(labels, labelClass) + falseNegative(labels, labelClass), 1.0)
  }

  def f1(labels: List[(String, String)], labelClass: String) : Double = {
    2 * precision(labels, labelClass) * recall(labels, labelClass) /
      math.max(precision(labels, labelClass) + recall(labels, labelClass), 1.0)
  }

  override def wikiSimPerformance() : WikiSimPerformance = {
    new WikiSimClassPerformance(scores)
  }

  override def toString() = "classification"
}
