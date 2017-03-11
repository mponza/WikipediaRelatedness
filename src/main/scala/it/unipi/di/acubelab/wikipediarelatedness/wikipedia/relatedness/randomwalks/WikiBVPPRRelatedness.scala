package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.randomwalks

import java.util.Locale

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessOptions}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.similarity.WikiBVPPR
import org.slf4j.Logger


/**
  * Relatedness based on random walks on the whole Wikipedia graph.
  *
  */
abstract class WikiBVPPRRelatedness(options: RelatednessOptions) extends Relatedness {
  protected val logger: Logger
  protected val wikiBVppr: WikiBVPPR


  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = {
    val s = wikiBVppr.similarity(srcWikiID, dstWikiID)
    logger.info("Similiarity between %d and %d is %1.5f".format(srcWikiID, dstWikiID, s))
    s.toFloat
  }


  override def toString() = "PPRCosRelatedness_iterations:%d,pprAlpha:%1.2f".formatLocal(Locale.US, options.iterations, options.pprAlpha)
}