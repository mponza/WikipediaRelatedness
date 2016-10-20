package it.unipi.di.acubelab.wikipediarelatedness.evaluation.correlation

import java.io.PrintWriter

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.{WikiSimEvaluator, WikiSimPerformance}
import org.apache.commons.math.stat.correlation.{PearsonsCorrelation, SpearmansCorrelation}
import org.slf4j.LoggerFactory



class WikiSimCorrelator(val tasks: List[WikiRelateTask]) extends WikiSimEvaluator {
  val logger = LoggerFactory.getLogger(classOf[WikiSimCorrelator])

  val pearson = pearsonCorrelation(tasks)
  val spearman = spearmanCorrelation(tasks)

  def pearsonCorrelation(tasks: List[WikiRelateTask]) : Double = {
    if(tasks.size <= 1) {
      logger.warn("(Pearson) Tasks with %d elements. -1 will be returned.".format(tasks.size))
      return -1.0
    }

    val (humanScores, relatedScores) = scoresToArrays(tasks)
    val pearson = new PearsonsCorrelation()

    pearson.correlation(humanScores, relatedScores)
  }

  def spearmanCorrelation(scores: List[WikiRelateTask]) : Double = {
    if(tasks.size <= 1) {
      logger.warn("(Spearman) Tasks with %d elements. -1 will be returned.".format(tasks.size))
      return -1.0
    }

    val (humanScores, relatedScores) = scoresToArrays(scores)
    val spearman = new SpearmansCorrelation()

    spearman.correlation(humanScores, relatedScores)
  }

  def scoresToArrays(scores: List[WikiRelateTask]) : (Array[Double], Array[Double]) = {
    val humanScores = scores.map(_.humanRelatedness).toArray
    val relatedScores = scores.map(_.computedRel).toArray

    (humanScores, relatedScores)
  }

  override def wikiSimPerformance(): WikiSimPerformance = {
    new WikiSimCorrPerformance(pearson, spearman)
  }

  override def toString() = "correlation"
}
