package it.unipi.di.acubelab.graphrel.analysis.bucket

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unipi.di.acubelab.graphrel.dataset.WikiRelTask
import it.unipi.di.acubelab.graphrel.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.graphrel.evaluation.{WikiSimEvaluatorFactory, WikiSimPerformance}

import scala.collection.mutable.ListBuffer

/**
  * Bucket analyzer. To perform statistics bucketized over different heuristic, just implement
  * bucketIndex function.
  */
trait BucketAnalyzer {
  val relatednessName: String
  val evalName: String
  val wikiSimDataset: WikiSimDataset

  val buckets = computeBuckets()//(for(i <- 0.0 to 1.0 - step by step) yield (i, i + step)).toList
  val bucketTasks = computeBucketTasks()

  val wikiSimPerformance = computePerformance()

  def computeBuckets(step: Double = 0.2) : List[(Double, Double)] = {
    (for(i <- 0.0 to 1.0 - step by step) yield (i, i + step)).toList
  }

  def computeBucketTasks() : Int2ObjectOpenHashMap[ObjectArrayList[WikiRelTask]] = {
    val bucketTasks = new Int2ObjectOpenHashMap[ObjectArrayList[WikiRelTask]]

    wikiSimDataset.foreach {
      wikiRelTask =>
        val index = bucketIndex(wikiRelTask)
        bucketTasks.putIfAbsent(index, new ObjectArrayList[WikiRelTask]())
        bucketTasks.get(index).add(wikiRelTask)
    }

    bucketTasks
  }

  /**
    *
    * @param wikiRelTask
    * @return Bucket index of wikiRelTask.
    */
  def bucketIndex(wikiRelTask: WikiRelTask) : Int

  def computePerformance() : List[WikiSimPerformance] = {
    val performance = ListBuffer.empty[WikiSimPerformance]

    for(i <- 0 until buckets.size) {

      if (bucketTasks.containsKey(i)) {

        val tasks = bucketTasks.get(i).asInstanceOf[List[WikiRelTask]]
        val evaluator = WikiSimEvaluatorFactory.make(evalName, tasks)
        performance += evaluator.wikiSimPerformance()

      } else {

        // If a key has no tasks.
        val evaluator = WikiSimEvaluatorFactory.make(evalName, List.empty[WikiRelTask])
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
