package it.unipi.di.acubelab.wikipediarelatedness.analysis.bucket.distance

import it.unipi.di.acubelab.wikipediarelatedness.analysis.bucket.BucketAnalyzer
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.WikiBVGraph


trait BucketDistanceAnalyzer extends BucketAnalyzer {
  def wikiBVGraph : WikiBVGraph
  def maxDistance = 3

  override def computeBuckets(step: Double) : List[(Double, Double)] = {
    (for(i <- 0.0 to maxDistance.toDouble by 1.0) yield (i, i)).toList  // Distances are ints.
  }

  def bucketIndex(wikiRelTask: WikiRelateTask) : Int = {
    val srcWikiID = wikiRelTask.src.wikiID
    val dstWikiID = wikiRelTask.dst.wikiID

    if(srcWikiID == dstWikiID) return 0

    val srcDist = wikiBVGraph.distance(srcWikiID, dstWikiID)
    val dstDist = wikiBVGraph.distance(dstWikiID, srcWikiID)

    val  dst = srcDist max dstDist

    println("Distance between %s is %d".format(wikiRelTask.wikiTitleString, dst))

    dst min maxDistance
  }
}
