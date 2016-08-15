package it.unipi.di.acubelab.graphrel.evaluation.correlation

import it.unipi.di.acubelab.graphrel.evaluation.WikiSimPerformance

class WikiSimCorrPerformance(val pearson: Double, val spearman: Double) extends WikiSimPerformance {

  override def toString: String = {
    "Pearson: %1.2f, Spearman: %1.2f".format(pearson, spearman)
  }
}
