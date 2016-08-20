package it.unipi.di.acubelab.wikipediarelatedness.evaluation

import java.nio.file.Paths

import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration


trait WikiSimEvaluator {
  def wikiSimPerformance() : WikiSimPerformance

  // Evaluator name
  def toString() : String
}
