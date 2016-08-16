package it.unipi.di.acubelab.graphrel.analysis.bucket.jaccard

import it.unipi.di.acubelab.graphrel.analysis.bucket.BucketAnalyzer
import it.unipi.di.acubelab.graphrel.dataset.WikiRelTask
import it.unipi.di.acubelab.graphrel.wikipedia.relatedness.Relatedness

trait BucketJaccardAnalyzer extends BucketAnalyzer {
  def jaccardRelatedness : Relatedness

  override def computeBuckets(step: Double = 0.1) : List[(Double, Double)] = {
    super.computeBuckets(step)
  }

  override def bucketIndex(wikiRelTask: WikiRelTask) : Int = {
    val ratio = jaccardRelatedness.computeRelatedness(wikiRelTask).toFloat

    for((bucket, index) <- buckets.zipWithIndex) {
      if (ratio >= bucket._1.toFloat && ratio <= bucket._2.toFloat) {
        return index
      }
    }
    throw new IllegalArgumentException("JaccardRatio error %.3f".format(ratio))
  }
}
