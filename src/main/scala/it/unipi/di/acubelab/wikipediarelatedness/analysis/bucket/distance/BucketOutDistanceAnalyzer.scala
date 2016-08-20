package it.unipi.di.acubelab.wikipediarelatedness.analysis.bucket.distance

import it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.WikiGraph

class BucketOutDistanceAnalyzer(val relatednessName: String, val evalName: String, val wikiSimDataset: WikiSimDataset)
  extends BucketDistanceAnalyzer {

  override def wikiBVGraph = WikiGraph.outGraph
}