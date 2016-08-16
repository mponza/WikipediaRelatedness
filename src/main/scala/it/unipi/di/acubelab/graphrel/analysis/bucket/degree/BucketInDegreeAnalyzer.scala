package it.unipi.di.acubelab.graphrel.analysis.bucket.degree

import it.unipi.di.acubelab.graphrel.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.graphrel.wikipedia.WikiGraph


class BucketInDegreeAnalyzer(val relatednessName: String, val evalName: String, val wikiSimDataset: WikiSimDataset)
  extends BucketDegreeAnalyzer {

  override def wikiBVGraph = WikiGraph.inGraph
}
