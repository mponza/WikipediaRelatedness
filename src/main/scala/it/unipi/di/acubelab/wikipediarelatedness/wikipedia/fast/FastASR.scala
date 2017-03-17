package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.io.BinIO
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory

/**
  * Faster implementation of the Algorithmic Scheme for computing Relatedness and evaluating its performance
  * with compressed/uncompressed data.
  */
class FastASR(milnewittenCompressed: Boolean = true, deepwalkCompressed: Boolean = false) extends Relatedness {
  protected val logger = LoggerFactory.getLogger(getClass)


  // maybe faster with int2bject[int2float]?
  val wikiID2ScoredOuts = BinIO.loadObject(Config.getString("wikipedia.cache.fast.storedout")).
                            asInstanceOf[Int2ObjectOpenHashMap[Array[(Int, Float)]]]

  val mw = new FastMilneWittenRelatedness(milnewittenCompressed)
  val dw = new FastDeepWalkRelatedness(deepwalkCompressed)


  override def toString = "FastASR_mw:%s,dw:%s" format (mw, dw)

  /**
    * Computes the relatedness between two Wikipedia entities uniquely identified by their ID.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = {

    val srcVec = wikiID2ScoredOuts.get(srcWikiID)
    val dstVec = wikiID2ScoredOuts.get(dstWikiID)

    // create vector and then do cosine (the cosine can be computed with one single scan)

  }
}
