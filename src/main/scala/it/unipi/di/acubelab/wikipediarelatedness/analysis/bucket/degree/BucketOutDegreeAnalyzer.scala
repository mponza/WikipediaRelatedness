package it.unipi.di.acubelab.wikipediarelatedness.analysis.bucket.degree

import it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.WikiGraph

class BucketOutDegreeAnalyzer(val relatednessName: String, val evalName: String, val wikiSimDataset: WikiSimDataset)
  extends BucketDegreeAnalyzer {

  override def wikiBVGraph = WikiGraph.outGraph
}