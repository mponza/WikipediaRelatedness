package it.unipi.di.acubelab.wikipediarelatedness.analysis.bucket.centrality

import java.io.FileWriter

import it.unipi.di.acubelab.wikipediarelatedness.analysis.bucket.BucketAnalyzer
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.WikiGraph

import scala.collection.mutable.ListBuffer


class BucketPageRankAnalyzer(val relatednessName: String, val evalName: String,
                             val wikiSimDataset: WikiSimDataset)
  extends BucketAnalyzer {

  def wikiBVGraph = WikiGraph.outGraph

  override def computeBuckets(step: Double) : List[(Double, Double)] = {
    rankingBuckets(wikiBVGraph.pageRanks, 4)
  }

  def rankingBuckets(ranks: Array[Double], numBuckets: Int) : List[(Double, Double)] = {
    val step = 0.000015

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

  def indexFromScore(score: Double) : Int = {
      for((bucket, index) <- buckets.zipWithIndex) {

        if (score >= bucket._1 && score <= bucket._2) {
          return index
        }
      }

      throw new IllegalArgumentException("PageRank error %.3f".format(score))
  }

  def bucketIndex(wikiRelTask: WikiRelateTask) : Int = {
    val srcWikiID = wikiRelTask.src.wikiID
    val dstWikiID = wikiRelTask.dst.wikiID

    val srcScore = wikiBVGraph.pageRankScore(srcWikiID)
    val dstScore = wikiBVGraph.pageRankScore(dstWikiID)

    //println("PR Scores of %s: %1.10f %1.10f".format(wikiRelTask.wikiTitleString(), srcScore, dstScore))
    indexFromScore(srcScore max dstScore)
  }
}