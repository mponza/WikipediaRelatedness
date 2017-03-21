package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast

import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast.wikiout.WikiOut
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph.SubNodeCreatorFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessFactory, RelatednessOptions}
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

/**
  * Faster implementation of the Algorithmic Scheme for computing Relatedness and evaluating its performance
  * with compressed/uncompressed data.
  */
class FastAlgorithmicScheme(milnewittenCompressed: Boolean = true, deepwalkCompressed: Boolean = false) extends Relatedness {
  protected val logger = LoggerFactory.getLogger(getClass)
  protected val outSize = 30

  val wikiOut = new WikiOut

  val mwdw = {
    val options = new RelatednessOptions(name="mix", lambda=0.5,
      firstname="milnewitten", firstgraph="in",
      secondname="w2v", secondmodel="deepwalk.dw10"
    )
    RelatednessFactory.make(options)
    //val mw = new FastMilneWittenRelatedness(milnewittenCompressed)
    //val dw = new FastDeepWalkRelatedness(deepwalkCompressed)

    //new FastMWDWRelatedness(mw, dw)
  }

  val subNodeCreator = SubNodeCreatorFactory.make("mw.out", 30)

  //override def toString = "FastASR_mw:%s,dw:%s" format (mw, dw)

  /**
    * Computes the relatedness between two Wikipedia entities uniquely identified by their ID.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    if (srcWikiID == dstWikiID) return 1f


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
        srcWeightVec(i) = mwdw.computeRelatedness(srcWikiID, nodeID)
        dstWeightVec(i) = mwdw.computeRelatedness(dstWikiID, nodeID)

        srcSum += srcWeightVec(i)
        dstSum += dstWeightVec(i)
      }

      if (nodeID == srcWikiID) {
        dstWeightVec(i) =  mwdw.computeRelatedness(dstWikiID, nodeID)
        dstSum += dstWeightVec(i)
        srcIndex = i
      }

      if(nodeID == dstWikiID) {
        srcWeightVec(i) = mwdw.computeRelatedness(srcWikiID, nodeID)
        srcSum += srcWeightVec(i)
        dstIndex = i
      }

      i += 1
    }
    srcSum = srcSum / 0.9f
    dstSum = dstSum / 0.9f


    // Cosine simliarity

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

  override def toString = "AlgorithmicScheme_%s" format mwdw.toString
}
