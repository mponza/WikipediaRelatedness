package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import java.util.Locale

import org.slf4j.LoggerFactory


/**
  * Use it CAREFULLY. Linear combination between two relatendess methods. Used as subgraph weighting.
  *
  * @param options
  */
class MixedRelatedness(options: RelatednessOptions) extends Relatedness {
  protected val logger = LoggerFactory.getLogger(getClass)

  protected val fst = RelatednessFactory.make( options.getFirstRelatednessOptions )
  protected val scd = RelatednessFactory.make( options.getSecondRelatednessOptions )
  protected val lambda = options.lambda.toFloat


  /**
    * Computes the relatedness between two Wikipedia entities uniquely identified by their ID.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = {
    lambda * fst.computeRelatedness(srcWikiID, dstWikiID) + (1 - lambda) * scd.computeRelatedness(srcWikiID, dstWikiID)
  }


  override def toString = "MixedRelatedness_first:%s,second:%s,lambda:%1.2f".formatLocal(Locale.US, fst, scd, lambda)
}
