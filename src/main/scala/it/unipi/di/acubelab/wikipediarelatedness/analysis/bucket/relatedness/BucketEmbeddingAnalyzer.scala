package it.unipi.di.acubelab.wikipediarelatedness.analysis.bucket.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.analysis.bucket.BucketAnalyzer
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessFactory

class BucketEmbeddingAnalyzer(val relatednessName: String, val evalName: String,
                              val wikiSimDataset: WikiSimDataset) extends BucketAnalyzer {


  def bucketIndex(wikiRelTask: WikiRelateTask) : Int = {
    val cosineSim = BucketEmbeddingAnalyzer.w2v.computeRelatedness(wikiRelTask)

    for((bucket, index) <- buckets.zipWithIndex) {
      if (wikiRelTask.humanRelatedness.toFloat >= bucket._1.toFloat && wikiRelTask.humanRelatedness.toFloat <= bucket._2.toFloat) {
        return index
      }
    }
    throw new IllegalArgumentException("W2V value %1.2f out of range".format(wikiRelTask.humanRelatedness))
  }
}

object BucketEmbeddingAnalyzer {
  lazy val w2v = RelatednessFactory.make(Some(Map("relatedness" -> "w2v")))
}