package it.unipi.di.acubelab.graphrel.evaluation

import it.unipi.di.acubelab.graphrel.dataset.WikiRelTask
import it.unipi.di.acubelab.graphrel.evaluation.classification.WikiSimClassificator
import it.unipi.di.acubelab.graphrel.evaluation.correlation.WikiSimCorrelator


object WikiSimEvaluatorFactory {
  def make(evaluatorName: String, tasks: List[WikiRelTask]) : WikiSimEvaluator = evaluatorName match {

    case "correlation" => new WikiSimCorrelator(tasks)
    case "classification" => new WikiSimClassificator(tasks)

    case _ => throw new IllegalArgumentException("Evaluator %s does not exist.".format(evaluatorName))
  }
}
