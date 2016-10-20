package it.unipi.di.acubelab.wikipediarelatedness.analysis.bucket.jaccard

import it.unipi.di.acubelab.wikipediarelatedness.analysis.bucket.BucketAnalyzer
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness

import scala.collection.mutable.ListBuffer

trait BucketJaccardAnalyzer extends BucketAnalyzer {
  def jaccardRelatedness : Relatedness

  override def computeBuckets(step: Double = 0.1) : List[(Double, Double)] = {
    jaccardBuckets(4, 0.001)
  }

  def jaccardBuckets(numBuckets: Int, step: Double) : List[(Double, Double)] = {

    var bi = 0.0
    val buckets = ListBuffer.empty[(Double, Double)]

    for(i <- 0 until numBuckets) {
      buckets += ((bi, bi + step))
      bi += step
    }
    buckets += ((bi, 1.0))

    println(buckets.toList)
    buckets.toList
  }

  override def bucketIndex(wikiRelTask: WikiRelateTask) : Int = {
    val ratio = jaccardRelatedness.computeRelatedness(wikiRelTask).toFloat

    for((bucket, index) <- buckets.zipWithIndex) {
      if (ratio >= bucket._1.toFloat && ratio <= bucket._2.toFloat) {
        return index
      }
    }
    throw new IllegalArgumentException("JaccardRatio error %.3f".format(ratio))
  }
}
