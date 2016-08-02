package it.unipi.di.acubelab.graphrel.analysis.bucket

import it.unipi.di.acubelab.graphrel.dataset.WikiRelTask
import it.unipi.di.acubelab.graphrel.dataset.wikisim.WikiSimDataset


class BucketRelAnalyzer(val relatednessName: String, val wikiSimDataset: WikiSimDataset) extends BucketAnalyzer {

  def bucketIndex(wikiRelTask: WikiRelTask) : Int = {
    for((bucket, index) <- buckets.zipWithIndex) {
      if (wikiRelTask.rel.toFloat >= bucket._1.toFloat && wikiRelTask.rel.toFloat <= bucket._2.toFloat) {
        return index
      }
    }
    throw new IllegalArgumentException("Relatedness value %1.2f out of range".format(wikiRelTask.rel))
  }
}