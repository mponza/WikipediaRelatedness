package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.graph.WikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.TwoStageFrameworkRelatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.firststage.FirstStageSubGraphCreation
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.firststage.nodes.RelatedWikiNeighbourNodesOfSubGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.firststage.nodes.cache.CachedNodesOfSubGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.secondstage.SecondStageComputingRelatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.secondstage.weights.RelatednessWeightsOfSubGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.secondstage.weights.cache.CachedWeightsOfSubGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.graphstructure.MilneWittenRelatedness

object WikiRelatednessFactory {

  /**
    * Milne&Witten relatedness method.
    *
    * @param inGraphFilename
    * @return
    */
  def makeMilneWitten(inGraphFilename: String) : WikiRelatedness = {
    new MilneWittenRelatedness( WikiGraph(inGraphFilename) )
  }


  /**
    * TwoStageFramework instantiated as
    *   - Top-k nodes selected from the out-links of a query entity and ranked with Milne&Witten
    *   - Weights from nodes are computed with Milne&Witten
    *
    * @param outGraphFilename
    * @param inGraphFilename
    * @param k
    * @return
    */
  def makeTwoStageFrameworkRelatedness(outGraphFilename: String, inGraphFilename: String,
                                       k: Int) : WikiRelatedness = {

    // First Stage
    val outGraph = WikiGraph(outGraphFilename)
    val mwRel = makeMilneWitten(inGraphFilename)
    val relOutNodesSubGraph = new RelatedWikiNeighbourNodesOfSubGraph(outGraph, mwRel)
    val firstStage = new FirstStageSubGraphCreation(relOutNodesSubGraph, k)

    // Second Stage
    val relWeightSubGraph = new RelatednessWeightsOfSubGraph(mwRel)
    val secondStage = new SecondStageComputingRelatedness(relWeightSubGraph)

    new TwoStageFrameworkRelatedness(firstStage, secondStage)
  }



  def makeCachedTwoStageFrameworkRelatedness(cacheTopNodesFilename:String,
                                             cacheWeightsFilename: String,
                                             outGraphFilename: String,
                                             inGraphFilename: String,
                                             k: Int) : WikiRelatedness = {
    // First Stage
    val mwRel = makeMilneWitten(inGraphFilename)
    val relOutNodesSubGraph = new RelatedWikiNeighbourNodesOfSubGraph(WikiGraph(outGraphFilename), mwRel)
    val cachedRelOutNodesSubGraph = new CachedNodesOfSubGraph(cacheTopNodesFilename, relOutNodesSubGraph)
    val firstStage = new FirstStageSubGraphCreation(relOutNodesSubGraph, k)

    // Second Stage
    val mwRelWeights = new RelatednessWeightsOfSubGraph(mwRel)
    val relWeightSubGraph = new CachedWeightsOfSubGraph(cacheWeightsFilename, mwRelWeights)
    val secondStage = new SecondStageComputingRelatedness(relWeightSubGraph)

    new TwoStageFrameworkRelatedness(firstStage, secondStage)
  }

}
