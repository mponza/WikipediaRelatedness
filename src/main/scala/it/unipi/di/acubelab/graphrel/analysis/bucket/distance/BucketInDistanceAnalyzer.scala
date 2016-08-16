package it.unipi.di.acubelab.graphrel.analysis.bucket.distance

import it.unipi.di.acubelab.graphrel.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.graphrel.wikipedia.WikiGraph

class BucketInDistanceAnalyzer(val relatednessName: String, val evalName: String, val wikiSimDataset: WikiSimDataset)
  extends BucketDistanceAnalyzer {

  override def wikiBVGraph = WikiGraph.inGraph
}
