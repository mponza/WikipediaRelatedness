package it.unipi.di.acubelab.wikipediarelatedness.analysis.bucket

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim.WikiSimDataset


class BucketRelAnalyzer(val relatednessName: String, val evalName: String, val wikiSimDataset: WikiSimDataset)
  extends BucketAnalyzer {

  def bucketIndex(wikiRelTask: WikiRelateTask) : Int = {
    for((bucket, index) <- buckets.zipWithIndex) {
      if (wikiRelTask.humanRelatedness.toFloat >= bucket._1.toFloat && wikiRelTask.humanRelatedness.toFloat <= bucket._2.toFloat) {
        return index
      }
    }
    throw new IllegalArgumentException("Relatedness value %1.2f out of range".format(wikiRelTask.humanRelatedness))
  }
}