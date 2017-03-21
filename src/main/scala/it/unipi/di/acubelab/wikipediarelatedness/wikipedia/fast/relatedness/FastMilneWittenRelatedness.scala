package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessFactory, RelatednessOptions}

class FastMilneWittenRelatedness(compressed: Boolean) extends Relatedness {

  val name = if (compressed) "milnewitten" else "uncom.mw"
  val options = new RelatednessOptions(name = name, graph = "in")
  val milneWitten = RelatednessFactory.make(options)


  /**
    * Computes the relatedness between two Wikipedia entities uniquely identified by their ID.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = milneWitten.computeRelatedness(srcWikiID, dstWikiID)

  override def toString = "FastMW_%s" format name
}
