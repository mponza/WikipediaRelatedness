package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.WikiRelatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.firststage.FirstStageSubGraphCreation
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.secondstage.SecondStageComputingRelatedness

/**
  * Optimized TwoStageFramework where the Wikipedia SubGraph is modeled as two different weighted vectors.
  */
class TwoStageFrameworkRelatedness(val firstStage: FirstStageSubGraphCreation,
                                    val secondStage: SecondStageComputingRelatedness) extends WikiRelatedness {

  override def relatedness(srcWikiID: Int, dstWikiID: Int): Float = {
    val wikiSubGraph = firstStage.growWikipediaSubGraph(srcWikiID, dstWikiID)
    val relatedness = secondStage.relatedness(wikiSubGraph)

    relatedness
  }

  override def name(): String = "TwoStageFramework"
}
