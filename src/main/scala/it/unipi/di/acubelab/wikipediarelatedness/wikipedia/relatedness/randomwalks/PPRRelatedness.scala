package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.randomwalks

import java.util.Locale

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{RelatednessOptions}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.similarity.WikiBVPPRCos
import org.slf4j.LoggerFactory

/**
  * Warning: pprAlpha needs to be set to the (1 - alpha).
  * @param options
  */
class PPRRelatedness(options: RelatednessOptions) extends WikiBVPPRRelatedness(options) {
  override protected val logger = LoggerFactory.getLogger(getClass)
  override protected val wikiBVppr = new WikiBVPPRCos(options.iterations, options.pprAlpha)


}
