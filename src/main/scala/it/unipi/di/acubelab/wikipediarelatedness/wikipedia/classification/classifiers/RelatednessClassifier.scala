package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.classification.classifiers

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelTask
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness

trait RelatednessClassifier {
  val relatedness: Relatedness

  def train(trainingData: List[WikiRelTask]) = {}

  def predict(evalData: List[WikiRelTask]) : List[WikiRelTask] = { evalData.map(predict(_)) }

  def predict(wikiRelTask: WikiRelTask): WikiRelTask
}
