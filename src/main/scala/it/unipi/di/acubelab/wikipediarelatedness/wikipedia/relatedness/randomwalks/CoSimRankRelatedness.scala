package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.randomwalks

import java.util.Locale

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessOptions}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.similarity.WikiBVCoSimRank
import org.slf4j.LoggerFactory

class CoSimRankRelatedness(options: RelatednessOptions) extends Relatedness {
  protected val logger = LoggerFactory.getLogger(getClass)
  protected val csr = new WikiBVCoSimRank(options.iterations, options.pprAlpha, options.csrDecay)

  /**
    * Computes the relatedness between two Wikipedia entities uniquely identified by their ID.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = {

    val s = csr.similarity(srcWikiID, dstWikiID)
    logger.info("Similiarity between %d and %d is %1.5f".format(srcWikiID, dstWikiID, s))
    s.toFloat
  }

  override def toString() = "CSRRelatedness_iters:%d,pprAlpha:%1.2f,csrDecay:%1.2f"
    .formatLocal(Locale.US, options.iterations, options.pprAlpha, options.csrDecay)

}
