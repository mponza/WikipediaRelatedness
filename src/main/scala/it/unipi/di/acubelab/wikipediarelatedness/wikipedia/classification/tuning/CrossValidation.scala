package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.classification.tuning

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelTask
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.classification.WikiSimClassPerformance
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.classification.WikiSimClassificator
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.classification.classifiers.RelatednessClassifier



trait CrossValidation {
  val wikiTaskFolds: WikiTaskFolds

  def computeCrossValidation(classificator: RelatednessClassifier) : WikiSimClassPerformance = {
    wikiTaskFolds.trainEvalTasks.map {

      case (train: List[WikiRelTask], eval: List[WikiRelTask]) =>

        classificator.train(train)
        val predictions = classificator.predict(eval)

        WikiSimCl
    }
  }
}
