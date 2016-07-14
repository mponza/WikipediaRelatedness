package it.unipi.di.acubelab.graphrel.benchmark

import it.unipi.di.acubelab.graphrel.dataset.WikiSimPair
import org.apache.commons.math.stat.correlation.{PearsonsCorrelation, SpearmansCorrelation}

object Evaluation {

  def pearsonCorrelation(scores: List[(WikiSimPair, Double)]) : Double = {
    val (humanScores, relatedScores) = scoresToArrays(scores)
    val pearson = new PearsonsCorrelation()

    pearson.correlation(humanScores, relatedScores)
  }

  def spearmanCorrelation(scores: List[(WikiSimPair, Double)]) : Double = {
    val (humanScores, relatedScores) = scoresToArrays(scores)
    val spearman = new SpearmansCorrelation()

    spearman.correlation(humanScores, relatedScores)
  }

  def scoresToArrays(scores: List[(WikiSimPair, Double)]) : (Array[Double], Array[Double]) = {
    val humanScores = scores.map(_._1.rel).toArray
    val relatedScores = scores.map(_._2).toArray

    (humanScores, relatedScores)
  }
}
