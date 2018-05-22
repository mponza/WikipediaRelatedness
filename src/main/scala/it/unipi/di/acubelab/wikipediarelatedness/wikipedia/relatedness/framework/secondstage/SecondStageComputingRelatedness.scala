package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.secondstage

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.secondstage.weights.WeightsOfSubGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.subgraph.{WeightedWikiSubGraph, WikiSubGraph}


class SecondStageComputingRelatedness(weightsOfSubGraph: WeightsOfSubGraph, k: Int) {

  def relatedness(wikiSubGraph: WikiSubGraph) : Float = {
    if (wikiSubGraph.srcWikiID.equals(wikiSubGraph.dstWikiID)) return 1f

    val weightedWikiSubGraph = edgeWeighting(wikiSubGraph)
    val relatedness = cosineSimilarity(weightedWikiSubGraph)

    relatedness
  }


  /**
    * Computes two vectors that respectively represent:
    *
    *   1. the weights between srcWikiID and the nodes in the graph (i.e. srcVec of WeightedWikiSubGraph)
    *   2. the weights between dstWikiID and the nodes in the graph (i.e. dstVec of WeightedWikiSubGraph)
    *
    * @param wikiSubGraph
    * @return
    */
  private def edgeWeighting(wikiSubGraph: WikiSubGraph) : WeightedWikiSubGraph = {

    val srcWikiID = wikiSubGraph.srcWikiID
    val dstWikiID = wikiSubGraph.dstWikiID
    val nodes = wikiSubGraph.nodeWikiIDs

    val srcWeightVec = Array.ofDim[Float](nodes.size())
    val dstWeightVec = Array.ofDim[Float](nodes.size())

    // For normalization. It starts by -0.1 because the sum of the weights
    // needs to be in [0, 0.9] plus 0.1 in srcWeightVec(srcIndex)
    var (srcSum, srcIndex) = (-0.1f, 0)
    var (dstSum, dstIndex) = (-0.1f, 0)
    val iterNode = nodes.iterator()

    var i = 0
    while(iterNode.hasNext) {
      val nodeID = iterNode.nextInt()

      if (nodeID != srcWikiID && nodeID != dstWikiID) {
        srcWeightVec(i) = weightsOfSubGraph.weighting(srcWikiID, nodeID)
        dstWeightVec(i) = weightsOfSubGraph.weighting(dstWikiID, nodeID)

        srcSum += srcWeightVec(i)
        dstSum += dstWeightVec(i)
      }

      if (nodeID == srcWikiID) {
        dstWeightVec(i) =  weightsOfSubGraph.weighting(dstWikiID, nodeID)
        dstSum += dstWeightVec(i)
        srcIndex = i
      }

      if(nodeID == dstWikiID) {
        srcWeightVec(i) = weightsOfSubGraph.weighting(srcWikiID, nodeID)
        srcSum += srcWeightVec(i)
        dstIndex = i
      }

      i += 1
    }
    srcSum = srcSum / 0.9f
    dstSum = dstSum / 0.9f

    new WeightedWikiSubGraph(srcWeightVec, srcSum, srcIndex,
                              dstWeightVec, dstSum, dstIndex)
  }


  /**
    * Cosine similarity between srcVec and dstVec of weightedWikiSubGraph.
    *
    * @param weightedWikiSubGraph
    * @return the final relatedness score of the TwoStageFramework
    */
  private def cosineSimilarity(weightedWikiSubGraph: WeightedWikiSubGraph) : Float = {
    val srcIndex = weightedWikiSubGraph.srcWikiIDIndex
    val dstIndex = weightedWikiSubGraph.dstWikiIDIndex

    val srcWeightVec = weightedWikiSubGraph.srcVec
    val dstWeightVec = weightedWikiSubGraph.dstVec

    val srcNorm = weightedWikiSubGraph.srcNorm
    val dstNorm = weightedWikiSubGraph.dstNorm

    var inner = 0f
    var (srcMagnitude, dstMagnitude) = (0f, 0f)
    var (srcWeight, dstWeight) = (0f, 0f)

    for(i <- 0 until k) {

      if(i != srcIndex && i != dstIndex) {
        srcWeight = srcWeightVec(i) / srcNorm
        dstWeight = dstWeightVec(i) / dstNorm
      }

      if(i == srcIndex) {
        srcWeight = 0.1f
        dstWeight = dstWeightVec(i) / dstNorm
      }

      if(i == dstIndex) {
        srcWeight = srcWeightVec(i) / srcNorm
        dstWeight = 0.1f
      }

      inner += srcWeight * dstWeight
      srcMagnitude += srcWeight * srcWeight
      dstMagnitude += dstWeight * dstWeight
    }

    inner / ( math.sqrt(srcMagnitude) * math.sqrt(dstMagnitude) ).toFloat
  }
}
