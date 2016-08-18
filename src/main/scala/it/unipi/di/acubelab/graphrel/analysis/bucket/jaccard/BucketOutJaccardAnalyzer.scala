package it.unipi.di.acubelab.graphrel.analysis.bucket.jaccard

import it.unipi.di.acubelab.graphrel.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.graphrel.wikipedia.WikiGraph
import it.unipi.di.acubelab.graphrel.wikipedia.relatedness.RelatednessFactory

class BucketOutJaccardAnalyzer(val relatednessName: String, val evalName: String, val wikiSimDataset: WikiSimDataset)
  extends BucketJaccardAnalyzer {

  override def jaccardRelatedness = RelatednessFactory.make(
    Some(Map("relatedness" -> "Jaccard", "graph" -> "outGraph")))

  override def computeBuckets(step: Double = 0.1) : List[(Double, Double)] = {
    jaccardBuckets(4, 0.005)
  }
}