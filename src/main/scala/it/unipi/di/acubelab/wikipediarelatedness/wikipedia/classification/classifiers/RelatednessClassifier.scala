package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.classification.classifiers

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness

trait RelatednessClassifier {
  val relatedness: Relatedness

  def train(trainingData: List[WikiRelateTask]) = {}

  def predict(evalData: List[WikiRelateTask]) : List[WikiRelateTask] = { evalData.map(predict(_)) }

  def predict(wikiRelTask: WikiRelateTask): WikiRelateTask
}
