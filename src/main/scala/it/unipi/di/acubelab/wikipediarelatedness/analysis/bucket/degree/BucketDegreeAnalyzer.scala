package it.unipi.di.acubelab.wikipediarelatedness.analysis.bucket.degree

import it.unipi.di.acubelab.wikipediarelatedness.analysis.bucket.BucketAnalyzer
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelTask
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.WikiBVGraph

trait BucketDegreeAnalyzer extends BucketAnalyzer {
  def wikiBVGraph : WikiBVGraph

  override def computeBuckets(step: Double = 0.1) : List[(Double, Double)] = {
    super.computeBuckets(step)
  }

  override def bucketIndex(wikiRelTask: WikiRelTask) : Int = {
    val ratio = degreeRatio(wikiRelTask)

    for((bucket, index) <- buckets.zipWithIndex) {
      if (ratio >= bucket._1.toFloat && ratio <= bucket._2.toFloat) {
        return index
      }
    }
    throw new IllegalArgumentException("DegreeRatio error %.3f".format(ratio))
  }

  def degreeRatio(wikiRelTask: WikiRelTask) : Float = {
    val srcInDegree = wikiBVGraph.outdegree(wikiRelTask.src.wikiID)
    val dstInDegree = wikiBVGraph.outdegree(wikiRelTask.dst.wikiID)

    if (srcInDegree == 0 || dstInDegree == 0) return 0f

    (srcInDegree min dstInDegree) / (srcInDegree max dstInDegree).toFloat
  }
}
