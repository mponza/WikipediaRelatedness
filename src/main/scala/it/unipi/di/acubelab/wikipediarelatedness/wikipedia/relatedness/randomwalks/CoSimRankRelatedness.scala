package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.randomwalks

import java.util.Locale

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.similarity.WikiBVCoSimRank
import org.slf4j.LoggerFactory


class CoSimRankRelatedness(options: RelatednessOptions) extends WikiBVPPRRelatedness(options) {
  override protected val logger = LoggerFactory.getLogger(getClass)
  override protected val wikiBVppr = new WikiBVCoSimRank(options.iterations, options.pprAlpha, options.csrDecay)

  override def toString = "CoSimRankRelatedness_iters:%d,pprAlpha:%1.2f,csrDecay:%1.2f"
    .formatLocal(Locale.US, options.iterations, options.pprAlpha, options.csrDecay)

}
