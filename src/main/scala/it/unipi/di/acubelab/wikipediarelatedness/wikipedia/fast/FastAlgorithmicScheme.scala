package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast

import it.unimi.dsi.fastutil.ints.{Int2ObjectOpenHashMap, IntOpenHashSet}
import it.unimi.dsi.fastutil.io.BinIO
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast.relatedness.{FastDeepWalkRelatedness, FastMWDWRelatedness, FastMilneWittenRelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast.wikiout.WikiOut
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory

/**
  * Faster implementation of the Algorithmic Scheme for computing Relatedness and evaluating its performance
  * with compressed/uncompressed data.
  */
class FastAlgorithmicScheme(milnewittenCompressed: Boolean = true, deepwalkCompressed: Boolean = false) extends Relatedness {
  protected val logger = LoggerFactory.getLogger(getClass)
  protected val outSize = 30

  val wikiOut = new WikiOut

  //val mw = new FastMilneWittenRelatedness(milnewittenCompressed)
  //val dw = new FastDeepWalkRelatedness(deepwalkCompressed)
  val mwdw = new FastMWDWRelatedness


  //override def toString = "FastASR_mw:%s,dw:%s" format (mw, dw)

  /**
    * Computes the relatedness between two Wikipedia entities uniquely identified by their ID.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int) = {

    val srcVec = wikiOut.topK(srcWikiID)
    val dstVec = wikiOut.topK(dstWikiID)


    // Create Set of unique Wikipedia nodes
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


    // Computes cosine similiarty where the score are given by their MWDWRelatedness
    var inner = 0f
    var srcMagnitude = 0f
    var dstMagnitude = 0f

    val iterNode = nodes.iterator()
    while(iterNode.hasNext) {
      val nodeID = iterNode.nextInt()
      val srcWeight = mwdw.computeRelatedness(srcWikiID, nodeID)
      val dstWeight = mwdw.computeRelatedness(srcWikiID, nodeID)
      inner += srcWeight * dstWeight

      srcMagnitude += srcWeight * srcWeight
      dstMagnitude += dstWeight * dstWeight
    }

    inner / ( Math.sqrt(srcMagnitude) * Math.sqrt(dstMagnitude) ).toFloat
  }
}
