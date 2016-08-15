package it.unipi.di.acubelab.graphrel.evaluation

import java.nio.file.Paths

import it.unipi.di.acubelab.graphrel.utils.Configuration


trait WikiSimEvaluator {
  def wikiSimPerformance() : WikiSimPerformance

  // Evaluator name
  def toString() : String
}
