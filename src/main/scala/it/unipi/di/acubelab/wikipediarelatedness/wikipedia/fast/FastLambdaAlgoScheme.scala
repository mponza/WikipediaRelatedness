package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast

import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast.relatedness.FastMWEmbeddingRelatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast.wikiout.WikiOut
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory


abstract class FastLambdaAlgoScheme extends Relatedness  {
  protected val logger = LoggerFactory.getLogger(getClass)
  protected val outSize = 30
  protected val nodeIDs = WikiBVGraphFactory.make("out").wiki2node

  val wikiOut = new WikiOut

  val mwEmbedWeighter = getMWEmbeddingWeighter()


  def getMWEmbeddingWeighter() : FastMWEmbeddingRelatedness


  def computeRelatedness(task: WikiRelateTask, lambda: Float) : Float = {
    val greaterZero = Math.max(computeRelatedness(task.src.wikiID, task.dst.wikiID, lambda), 0f)
    val lowerOne = Math.min(greaterZero, 1f)

    lowerOne
  }


  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = computeRelatedness(srcWikiID, dstWikiID, 0.5f)


  def computeRelatedness(srcWikiID: Int, dstWikiID: Int, lambda: Float): Float = {
    if (srcWikiID == dstWikiID) return 1f


    if (!nodeIDs.containsKey(srcWikiID)) { logger.warn("[Lambda] src: %d not present." format srcWikiID); return 0f }
    if (!nodeIDs.containsKey(dstWikiID)) { logger.warn("[Lambda] dst: %d not present." format dstWikiID); return 0f }


    // Creation of the set of unique nodes of the Wikipedia subgraph

    val srcVec = wikiOut.topK(srcWikiID)
    val dstVec = wikiOut.topK(dstWikiID)

    val nodes = new IntOpenHashSet()

    nodes.add(srcWikiID)
    nodes.add(dstWikiID)

    var (i, j) = (0, 0)
    var n = nodes.size()

    while(i < srcVec.length && j < dstVec.length && nodes.size() < outSize) {

      while (i < srcVec.length && n == nodes.size() && nodes.size() < outSize) {
        nodes.add( srcVec(i) )
        i += 1
      }
      if (n != nodes.size()) n += 1

      while (j < dstVec.length && n == nodes.size() && nodes.size() < outSize) {
        nodes.add( dstVec(j) )
        j += 1
      }
      if (n != nodes.size()) n += 1

    }
    while (i < srcVec.length && nodes.size() < outSize) { nodes.add( srcVec(i) ) ; i += 1 }
    while (j < dstVec.length && nodes.size() < outSize) { nodes.add( dstVec(j) ) ; j += 1 }


    // Weighting of the Wikipedia subgraph

    val srcWeightVec = Array.ofDim[Float](nodes.size())
    val dstWeightVec = Array.ofDim[Float](nodes.size())

    // For normalization. It starts by -0.1 because the sum of the weights needs to be in [0, 0.9]
    // plus 0.1 in srcWeightVec(srcIndex)
    var (srcSum, srcIndex) = (-0.1f, 0)
    var (dstSum, dstIndex) = (-0.1f, 0)
    val iterNode = nodes.iterator()
    i = 0
    while(iterNode.hasNext) {
      val nodeID = iterNode.nextInt()

      if (nodeID != srcWikiID && nodeID != dstWikiID) {
        srcWeightVec(i) = mwEmbedWeighter.computeRelatedness(srcWikiID, nodeID, lambda)
        dstWeightVec(i) = mwEmbedWeighter.computeRelatedness(dstWikiID, nodeID, lambda)

        srcSum += srcWeightVec(i)
        dstSum += dstWeightVec(i)
      }

      if (nodeID == srcWikiID) {
        dstWeightVec(i) =  mwEmbedWeighter.computeRelatedness(dstWikiID, nodeID, lambda)
        dstSum += dstWeightVec(i)
        srcIndex = i
      }

      if(nodeID == dstWikiID) {
        srcWeightVec(i) = mwEmbedWeighter.computeRelatedness(srcWikiID, nodeID, lambda)
        srcSum += srcWeightVec(i)
        dstIndex = i
      }

      i += 1
    }
    srcSum = srcSum / 0.9f
    dstSum = dstSum / 0.9f


    // Cosine similarity

    var inner = 0f
    var (srcMagnitude, dstMagnitude) = (0f, 0f)
    var (srcWeight, dstWeight) = (0f, 0f)

    for(i <- 0 until nodes.size()) {

      if(i != srcIndex && i != dstIndex) {
        srcWeight = srcWeightVec(i) / srcSum
        dstWeight = dstWeightVec(i) / dstSum
      }

      if(i == srcIndex) {
        srcWeight = 0.1f
        dstWeight = dstWeightVec(i) / dstSum
      }

      if(i == dstIndex) {
        srcWeight = srcWeightVec(i) / srcSum
        dstWeight = 0.1f
      }

      inner += srcWeight * dstWeight
      srcMagnitude += srcWeight * srcWeight
      dstMagnitude += dstWeight * dstWeight
    }

    inner / ( math.sqrt(srcMagnitude) * math.sqrt(dstMagnitude) ).toFloat

  }

  override def toString = "AlgorithmicScheme_%s" format mwEmbedWeighter.toString

}