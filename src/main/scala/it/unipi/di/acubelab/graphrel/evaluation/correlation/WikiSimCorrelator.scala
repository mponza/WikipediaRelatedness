package it.unipi.di.acubelab.graphrel.evaluation.correlation

import java.io.PrintWriter

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unipi.di.acubelab.graphrel.dataset.WikiRelTask
import it.unipi.di.acubelab.graphrel.evaluation.{WikiSimEvaluator, WikiSimPerformance}
import org.apache.commons.math.stat.correlation.{PearsonsCorrelation, SpearmansCorrelation}
import org.slf4j.LoggerFactory



class WikiSimCorrelator(val tasks: List[WikiRelTask]) extends WikiSimEvaluator {
  val logger = LoggerFactory.getLogger(classOf[WikiSimCorrelator])

  val pearson = pearsonCorrelation(tasks)
  val spearman = spearmanCorrelation(tasks)

  def pearsonCorrelation(tasks: List[WikiRelTask]) : Double = {
    if(tasks.size <= 1) {
      logger.warn("(Pearson) Tasks with %d elements. -1 will be returned.".format(tasks.size))
      return -1.0
    }

    val (humanScores, relatedScores) = scoresToArrays(tasks)
    val pearson = new PearsonsCorrelation()

    pearson.correlation(humanScores, relatedScores)
  }

  def spearmanCorrelation(scores: List[WikiRelTask]) : Double = {
    if(tasks.size <= 1) {
      logger.warn("(Spearman) Tasks with %d elements. -1 will be returned.".format(tasks.size))
      return -1.0
    }

    val (humanScores, relatedScores) = scoresToArrays(scores)
    val spearman = new SpearmansCorrelation()

    spearman.correlation(humanScores, relatedScores)
  }

  def scoresToArrays(scores: List[WikiRelTask]) : (Array[Double], Array[Double]) = {
    val humanScores = scores.map(_.rel).toArray
    val relatedScores = scores.map(_.computedRel).toArray

    (humanScores, relatedScores)
  }

  def writeTmpScores(scores: Array[Double], path: String = "tmp/rel.txt") : Unit = {
    val pw = new PrintWriter(path)
    pw.write(scores.mkString(","))
    pw.close
  }

  def objectArrayList2List(objectArrayList : ObjectArrayList[WikiRelTask]) : List[WikiRelTask] = {
    val arrayTasks = Array.ofDim[WikiRelTask](objectArrayList.size)
    objectArrayList.toArray[WikiRelTask](arrayTasks).toList
  }

  override def wikiSimPerformance(): WikiSimPerformance = {
    new WikiSimCorrPerformance(pearson, spearman)
  }

  override def toString() = "correlation"
}
