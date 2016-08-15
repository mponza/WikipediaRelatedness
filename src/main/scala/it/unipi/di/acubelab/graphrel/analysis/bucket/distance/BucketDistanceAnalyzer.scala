package it.unipi.di.acubelab.graphrel.analysis.bucket.distance

import it.unipi.di.acubelab.graphrel.analysis.bucket.BucketAnalyzer
import it.unipi.di.acubelab.graphrel.dataset.WikiRelTask
import it.unipi.di.acubelab.graphrel.wikipedia.processing.webgraph.WikiBVGraph

trait BucketDistanceAnalyzer extends BucketAnalyzer {
  def wikiBVGraph : WikiBVGraph

  def computeBuckets(step: Double, maxDistance: Int = 4) : List[(Double, Double)] = {
    (for(i <- 0.0 to maxDistance.toDouble - step by step) yield (i, i)).toList  // Distances are ints.
  }

  def bucketIndex(wikiRelTask: WikiRelTask) : Int = {
    val srcWikiID = wikiRelTask.src.wikiID
    val dstWikiID = wikiRelTask.dst.wikiID

    val srcDist = wikiBVGraph.distance(srcWikiID, dstWikiID)
    val dstDist = wikiBVGraph.distance(dstWikiID, srcWikiID)

    val  dst = srcDist max dstDist

    dst
  }
}
