package it.unipi.di.acubelab.wikipediarelatedness.evaluation.classification

import it.unipi.di.acubelab.wikipediarelatedness.evaluation.WikiSimPerformance

class WikiSimClassPerformance(val precision: Double, recall: Double, f1: Double) extends WikiSimPerformance {

  override def toString: String = {
    "Precision: %1.2f, Recall: %1.2f, F1: %1.2f".format(precision, recall, f1)
  }

  def csvFields() : List[String] = { List ("Precision", "Recall", "F1") }
  def csvValues() : List[Double] = { List(precision, recall, f1)}
}
