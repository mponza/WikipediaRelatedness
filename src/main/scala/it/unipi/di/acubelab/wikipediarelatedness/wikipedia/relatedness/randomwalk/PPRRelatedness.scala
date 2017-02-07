package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.randomwalk

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.randomwalk.CoSimRankCache
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessOptions}
import org.slf4j.LoggerFactory


/**
  * PPRRelatedness over the whole Wikipedia graph. Computed by using CoSimRankServer.
  *
  * @param options
  */
class PPRRelatedness(options: RelatednessOptions) extends Relatedness {
  protected val logger = LoggerFactory.getLogger(getClass)
  protected val ppr = new CoSimRankCache(options.pprAlpha, options.iterations)

  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = ppr.getPPRCos(srcWikiID, dstWikiID)

  override def toString () : String = { "PPR_decay:%1.2f,iterations%d".format(options.pprAlpha, options.iterations) }
}
