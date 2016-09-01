package it.unipi.di.acubelab.wikipediarelatedness.evaluation.classification

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelTask
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.{WikiSimEvaluator, WikiSimPerformance}

class WikiSimClassificator (val tasks: List[WikiRelTask]) extends WikiSimEvaluator {

  tasks.foreach(task => if(task.humanLabelClass < 0 || task.computedLabelClass < 0)
                            throw new IllegalArgumentException("Class label with negative value."))

  override def wikiSimPerformance(): WikiSimPerformance = {
    new WikiSimClassPerformance(precision(), recall(), f1())
  }

  def truePositive(): Int = {
    tasks.count(task => task.humanLabelClass == task.computedLabelClass && task.humanLabelClass == 1)
  }

  def trueNegative(): Int = {
    tasks.count(task => task.humanLabelClass == task.computedLabelClass && task.humanLabelClass == 0)
  }

  def falsePositive(): Int = {
    tasks.count(task => task.humanLabelClass != task.computedLabelClass && task.computedLabelClass == 1)
  }

  def falseNegative(): Int = {
    tasks.count(task => task.humanLabelClass != task.computedLabelClass && task.computedLabelClass == 0)
  }

  def precision() : Double = {
    val tp = truePositive().toDouble
    val fp = falsePositive()

    if (tp == 0.0) return 0.0

    tp / (tp + fp)
  }

  def recall() : Double = {
    val tp =  truePositive().toDouble
    val fn = falseNegative()

    if (tp == 0f) return 0f

    tp / (tp + fn)
  }

  def f1() : Double = {
    val prec = precision()
    val rec = recall()

    if (prec == 0.0 || rec == 0.0) return 0.0

    2 * prec * rec / (prec + rec)
  }
}
