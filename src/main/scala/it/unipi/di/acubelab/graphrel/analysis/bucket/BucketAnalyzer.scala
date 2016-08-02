package it.unipi.di.acubelab.graphrel.analysis.bucket

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unipi.di.acubelab.graphrel.benchmark.Evaluation
import it.unipi.di.acubelab.graphrel.dataset.WikiRelTask
import it.unipi.di.acubelab.graphrel.dataset.wikisim.WikiSimDataset

import scala.collection.mutable.ListBuffer

/**
  * Bucket analyzer. To perform statistics bucketized over different heuristic, just implement
  * bucketIndex function.
  */
trait BucketAnalyzer {
  val relatednessName: String
  val wikiSimDataset: WikiSimDataset

  val step = 0.20

  val buckets = (for(i <- 0.0 to 1.0 - step by step) yield (i, i + step)).toList
  val bucketTasks = computeBucketTasks()

  // Correlation score lists.
  val pearsons = computePearsons()
  val spearmans = computeSpearmans()

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

  def computeCorrelation(corrFun: ObjectArrayList[WikiRelTask] => Double) : List[Double] = {
    val correlations = ListBuffer.empty[Double]

    for(i <- 0 until buckets.size) {

      if (bucketTasks.containsKey(i))  {
        val tasks = bucketTasks.get(i)
        val correlation = corrFun(tasks)
        correlations += correlation

      } else {
        correlations += 0.0
      }
    }

    correlations.toList
  }

  def computePearsons() : List[Double] = {
    computeCorrelation(Evaluation.pearsonCorrelation)
  }

  def computeSpearmans() : List[Double] = {
    computeCorrelation(Evaluation.spearmanCorrelation)
  }

  def toCSVList(bucketIndex: Int): List[Any] = {
    List(relatednessName, pearsons(bucketIndex), spearmans(bucketIndex))
  }
}
