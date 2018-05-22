package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.secondstage.weights

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.WikiRelatedness

class RelatednessWeightsOfSubGraph(wikiRelatedness: WikiRelatedness) extends WeightsOfSubGraph {

  override def weighting(srcWikiID: Int, dstWikiID: Int): Float = {
    wikiRelatedness.relatedness(srcWikiID, dstWikiID)
  }
}
