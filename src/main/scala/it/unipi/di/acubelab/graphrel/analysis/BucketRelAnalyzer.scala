package it.unipi.di.acubelab.graphrel.analysis

import com.github.tototoshi.csv.CSVWriter
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unipi.di.acubelab.graphrel.benchmark.Evaluation
import it.unipi.di.acubelab.graphrel.dataset.WikiRelTask
import it.unipi.di.acubelab.graphrel.dataset.wikisim.WikiSimDataset

import scala.collection.mutable.ListBuffer


class BucketRelAnalyzer(relatednessName: String, wikiSimDataset: WikiSimDataset) {
  val step = 0.25
  // Relatedness buckets.
  val buckets = (for(i <- 0.0 to 1.0 - step by step) yield (i, i + step)).toList
  val bucketTasks = computeBucketTasks()

  /*for(i <- 0 until buckets.size) println("%d with size: %d.".format(i, bucketTasks.get(i).size))

  val writer = CSVWriter.open("/tmp/prova.csv")
  for(i <- 0 until bucketTasks.get(4).size) {
    writer.writeRow(bucketTasks.get(4).get(i).toList)
  }*/

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

  def bucketIndex(wikiRelTask: WikiRelTask) : Int = {
    for((bucket, index) <- buckets.zipWithIndex) {
      if (wikiRelTask.rel.toFloat >= bucket._1.toFloat && wikiRelTask.rel.toFloat <= bucket._2.toFloat) {
        return index
      }
    }
    throw new IllegalArgumentException("Relatedness value %1.2f out of range".format(wikiRelTask.rel))
  }

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