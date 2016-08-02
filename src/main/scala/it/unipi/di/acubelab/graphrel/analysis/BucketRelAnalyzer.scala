/*package it.unipi.di.acubelab.graphrel.analysis

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unipi.di.acubelab.graphrel.analysis.wikisim.WikiBenchTask


class BucketRelAnalyzer(relatednessName: String) {
  val step = 0.25
  // Relatedness buckets.
  val buckets = (for(i <- 0.0 to 1.0 - step by step) yield (i, i + step)).toList

  // wikiBenchTask for each bucket.
  val bucketTasks = new Int2ObjectOpenHashMap[ObjectArrayList[WikiBenchTask]]

  def add(wikiBenchTask: WikiBenchTask) = {
    val index = bucketIndex(wikiBenchTask.rel)
    bucketTasks.putIfAbsent(index, new ObjectArrayList[WikiBenchTask]())
    bucketTasks.get(index).add(wikiBenchTask)
  }

  def bucketIndex(relatedness: Double) : Int = {
    for((bucket, index) <- buckets.zipWithIndex) {
      if (relatedness >= bucket._1 && relatedness <= bucket._2) {
        return index
      }
    }
    throw new IllegalArgumentException("Relatedness value %1.2f out of range".format(relatedness))
  }

  def computePearsons() : List[Double] = {
    for(i <- 0 until bucketTasks.size) {
      val benchTasks = bucketTasks
    }
  }

  def computeSpearmans() : List[Double] = {

  }

  override def toString(): String = {
    "BucketRelAnalyzer-%s".format(relatednessName)
  }
}
*/