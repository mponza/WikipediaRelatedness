package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast.wikiout.WikiOut
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness

class FastMWDWRelatedness(milneWitten: FastMilneWittenRelatedness, deepWalk: FastDeepWalkRelatedness) extends Relatedness {
  protected val wikiout = new WikiOut

  /**
    * Computes the relatedness between two Wikipedia entities uniquely identified by their ID.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = {
    val precompRel = wikiout.relatedness(srcWikiID, dstWikiID)
    if(precompRel >= 0f) return precompRel

    0.5f * milneWitten.computeRelatedness(srcWikiID, dstWikiID) * deepWalk.computeRelatedness(srcWikiID, dstWikiID)
  }


  override def toString() = "MWDW_[MW:%s, DW: %s]" format (milneWitten, deepWalk)
}
