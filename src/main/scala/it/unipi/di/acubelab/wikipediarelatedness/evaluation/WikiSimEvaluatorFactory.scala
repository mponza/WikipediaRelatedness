package it.unipi.di.acubelab.wikipediarelatedness.evaluation

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelTask
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.bucketclassification.WikiSimBucketClassificator
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.correlation.WikiSimCorrelator


object WikiSimEvaluatorFactory {
  def make(evaluatorName: String, tasks: List[WikiRelTask]) : WikiSimEvaluator = evaluatorName match {

    case "correlation" => new WikiSimCorrelator(tasks)
    case "classification" => new WikiSimBucketClassificator(tasks)

    case _ => throw new IllegalArgumentException("Evaluator %s does not exist.".format(evaluatorName))
  }
}
