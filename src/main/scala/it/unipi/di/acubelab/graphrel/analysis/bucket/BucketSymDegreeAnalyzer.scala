package it.unipi.di.acubelab.graphrel.analysis.bucket

import it.unipi.di.acubelab.graphrel.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.graphrel.wikipedia.WikiGraph

class BucketSymDegreeAnalyzer(val relatednessName: String, val wikiSimDataset: WikiSimDataset)
  extends BucketDegreeAnalyzer {

  override def wikiBVGraph = WikiGraph.symGraph
}
