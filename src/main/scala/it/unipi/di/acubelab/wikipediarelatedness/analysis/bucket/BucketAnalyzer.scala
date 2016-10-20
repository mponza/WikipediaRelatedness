package it.unipi.di.acubelab.wikipediarelatedness.analysis.bucket

import java.io.FileWriter

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.WikiGraph

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashMap

/**
  * Bucket analyzer. To perform statistics bucketized over different heuristic, just implement
  * bucketIndex function.
  */
trait BucketAnalyzer {
  val relatednessName: String
  val evalName: String
  val wikiSimDataset: WikiSimDataset

  val buckets = computeBuckets()
  val bucketTasks = computeBucketTasks()

  val wikiSimPerformance = computePerformance()

  def computeBuckets(step: Double = 0.2) : List[(Double, Double)] = {
    (for(i <- 0.0 to 1.0 - step by step) yield (i, i + step)).toList
  }

  def computeBucketTasks() : Map[Int, List[WikiRelateTask]] = {
    val bucketTasks = HashMap.empty[Int, ListBuffer[WikiRelateTask]]

    wikiSimDataset.foreach {
      wikiRelTask =>
        val index = bucketIndex(wikiRelTask)

        if(index >= 0) { // Skips negative indices.
          val tasks = bucketTasks.getOrElse(index, ListBuffer.empty[WikiRelateTask])
          tasks += wikiRelTask
          bucketTasks.put(index, tasks)
        }
    }

    bucketTasks.mapValues(_.toList).toMap
  }

  /**
    *
    * @param wikiRelTask
    * @return Bucket index of wikiRelTask.
    */
  def bucketIndex(wikiRelTask: WikiRelateTask) : Int

  def computePerformance() : List[WikiSimPerformance] = {
    val performance = ListBuffer.empty[WikiSimPerformance]

    for(i <- 0 until buckets.size) {

      if (bucketTasks.contains(i)) {

        val tasks = bucketTasks(i)
        val evaluator = WikiSimEvaluatorFactory.make(evalName, tasks)
        performance += evaluator.wikiSimPerformance()

      }
      else {

        // If a key has no tasks.
        val evaluator = WikiSimEvaluatorFactory.make(evalName, List.empty[WikiRelateTask])
        performance += evaluator.wikiSimPerformance()
      }
    }

    performance.toList
  }

  override def toString: String = ""

  def bucketToCSVRow(index: Int) : List[Any] = {
    relatednessName +: wikiSimPerformance(index).csvValues()
  }
}
