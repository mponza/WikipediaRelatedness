package it.unipi.di.acubelab.graphrel.analysis.bucket.distance

import it.unipi.di.acubelab.graphrel.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.graphrel.wikipedia.WikiGraph

class BucketSymDistanceAnalyzer(val relatednessName: String, val wikiSimDataset: WikiSimDataset)
  extends BucketDistanceAnalyzer {

  override def wikiBVGraph = WikiGraph.symGraph
}
