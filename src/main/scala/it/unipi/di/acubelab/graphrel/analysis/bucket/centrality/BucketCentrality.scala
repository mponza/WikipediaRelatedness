package it.unipi.di.acubelab.graphrel.analysis.bucket.centrality

import it.unipi.di.acubelab.graphrel.analysis.bucket.BucketAnalyzer
import it.unipi.di.acubelab.graphrel.dataset.WikiRelTask
import it.unipi.di.acubelab.graphrel.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.graphrel.wikipedia.WikiGraph


class BucketPageRankAnalyzer(val relatednessName: String, val evalName: String,
                             val wikiSimDataset: WikiSimDataset)
  extends BucketAnalyzer {

  def wikiBVGraph = WikiGraph.outGraph

  override def computeBuckets(step: Double) : List[(Double, Double)] = {
    super.computeBuckets()
  }

  def indexFromScore(score: Double) : Int = {
      for((bucket, index) <- buckets.zipWithIndex) {

        if (score >= bucket._1 && score <= bucket._2) {
          return index
        }
      }

      throw new IllegalArgumentException("DegreeRatio error %.3f".format(score))
  }

  def bucketIndex(wikiRelTask: WikiRelTask) : Int = {
    val srcWikiID = wikiRelTask.src.wikiID
    val dstWikiID = wikiRelTask.dst.wikiID

    val srcScore = wikiBVGraph.pageRankScore(srcWikiID)
    val dstScore = wikiBVGraph.pageRankScore(dstWikiID)

    println("PR Scores of %s: %1.2f %1.2f".format(wikiRelTask.wikiTitleString(), srcScore, dstScore))
    indexFromScore(srcScore max dstScore)
  }
}