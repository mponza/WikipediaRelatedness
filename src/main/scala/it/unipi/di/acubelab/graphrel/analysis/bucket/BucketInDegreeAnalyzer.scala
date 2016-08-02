package it.unipi.di.acubelab.graphrel.analysis.bucket

import it.unipi.di.acubelab.graphrel.dataset.WikiRelTask
import it.unipi.di.acubelab.graphrel.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.graphrel.wikipedia.WikiGraph

class BucketInDegreeAnalyzer(val relatednessName: String, val wikiSimDataset: WikiSimDataset)
  extends BucketAnalyzer {

  override def bucketIndex(wikiRelTask: WikiRelTask) : Int = {
    val ratio = inDegreeRatio(wikiRelTask)

    for((bucket, index) <- buckets.zipWithIndex) {
      if (ratio >= bucket._1.toFloat && ratio <= bucket._2.toFloat) {
        return index
      }
    }
    throw new IllegalArgumentException("InDegreeRatio error %1.2f".format(ratio))
  }

  def inDegreeRatio(wikiRelTask: WikiRelTask) : Float = {
    val srcInDegree = WikiGraph.inGraph.outdegree(wikiRelTask.src.wikiID)
    val dstInDegree = WikiGraph.inGraph.outdegree(wikiRelTask.dst.wikiID)

    if (srcInDegree == 0 || dstInDegree == 0) return 0f

    (srcInDegree max dstInDegree) / (srcInDegree min dstInDegree).toFloat
  }
}
