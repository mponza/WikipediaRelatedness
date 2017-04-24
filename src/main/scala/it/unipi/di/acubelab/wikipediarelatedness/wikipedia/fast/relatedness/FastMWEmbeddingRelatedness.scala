package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast.wikiout.WikiOut
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness

class FastMWEmbeddingRelatedness(milneWitten: FastMilneWittenRelatedness, embeddingRelatedness: FastEmbeddingRelatedness) extends Relatedness {
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

    0.5f * (milneWitten.computeRelatedness(srcWikiID, dstWikiID) + embeddingRelatedness.computeRelatedness(srcWikiID, dstWikiID) )
  }


  /**
    * As computeRelatedness but with a configurable lambda weight.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @param lambda
    * @return
    */
  def computeRelatedness(srcWikiID: Int, dstWikiID: Int, lambda: Float) : Float = {
    lambda * milneWitten.computeRelatedness(srcWikiID, dstWikiID) + (1f - lambda) * embeddingRelatedness.computeRelatedness(srcWikiID, dstWikiID)
  }


  def computeRelatedness(task: WikiRelateTask, lambda: Float) : Float = {
    val greaterZero = Math.max(computeRelatedness(task.src.wikiID, task.dst.wikiID, lambda), 0f)
    val lowerOne = Math.min(greaterZero, 1f)

    lowerOne
  }


  override def toString() = "MWDW_[MW:%s, DW: %s]" format (milneWitten, embeddingRelatedness)
}
