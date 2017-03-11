package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.randomwalks

import java.util.Locale

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.similarity.WikiBVWikiWalk
import org.slf4j.LoggerFactory


class WikiWalkRelatedness(options: RelatednessOptions) extends WikiBVPPRRelatedness(options) {
  override protected val logger = LoggerFactory.getLogger(getClass)
  override protected val wikiBVppr = new WikiBVWikiWalk(options.iterations, options.pprAlpha)

  override def toString = "WikiWalkRelatedness_iters:%d,pprAlpha:%1.2f"
    .formatLocal(Locale.US, options.iterations, options.pprAlpha, options.csrDecay)

}
