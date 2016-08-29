package it.unipi.di.acubelab.wikipediarelatedness.evaluation.classification

import java.lang.Double
import scala.language.existentials

import com.google.common.collect.Range
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelTask
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.WikiSimEvaluator
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.WikiSimPerformance

class WikiSimClassificator(val tasks: List[WikiRelTask]) extends WikiSimEvaluator {

  def scores = classificationScores()

  /**
    * @return {"low/medium/high" -> {"F1/Precision/Recall" -> Double}}
    * */
  def classificationScores() : Map[String, Map[String, Double]] = {
    val buckets = Map("low" -> Range.closed(new Double(0.0), new Double(0.3)),
                      "medium" -> Range.openClosed(new Double(0.3), new Double(0.6)),
                      "high" -> Range.openClosed(new Double(0.6), new Double(1.0)))

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
    val tp = truePositive(labels, labelClass).toDouble
    val fp = falsePositive(labels, labelClass)

    if (tp == 0.0) return 0.0

    tp / (tp + fp)
  }

  def recall(labels: List[(String, String)], labelClass: String) : Double = {
    val tp =  truePositive(labels, labelClass).toDouble
    val fn = falseNegative(labels, labelClass)

    if (tp == 0.0) return 0.0

    tp / (tp + fn)
  }

  def f1(labels: List[(String, String)], labelClass: String) : Double = {
    val prec = precision(labels, labelClass)
    val rec = recall(labels, labelClass)

    if (prec == 0.0 || rec == 0.0) return 0.0

    2 * prec * rec / (prec + rec)
  }

  override def wikiSimPerformance() : WikiSimPerformance = {
    new WikiSimClassPerformance(scores)
  }

  override def toString() = "classification"
}
