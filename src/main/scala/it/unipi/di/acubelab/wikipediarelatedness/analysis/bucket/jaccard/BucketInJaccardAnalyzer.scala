package it.unipi.di.acubelab.wikipediarelatedness.analysis.bucket.jaccard

import it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessFactory


class BucketInJaccardAnalyzer(val relatednessName: String, val evalName: String, val wikiSimDataset: WikiSimDataset)
  extends BucketJaccardAnalyzer {

  override def jaccardRelatedness = RelatednessFactory.make(
    Some(Map("relatedness" -> "Jaccard", "graph" -> "inGraph")))
}
