package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.randomwalk

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.randomwalk.CoSimRankCache
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessOptions}
import org.slf4j.LoggerFactory


/**
  * CoSimRank relatedness over the whole graph. Computed by using CoSimRankServer.
  *
  * @param options
  */
class CoSimRankRelatedness(options: RelatednessOptions) extends Relatedness {
  protected val logger = LoggerFactory.getLogger(getClass)
  protected val csr = new CoSimRankCache(options.pprAlpha, options.iterations)

  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = csr.getCoSimRank(srcWikiID, dstWikiID)

  override def toString () : String = { "CoSimRank_decay:%1.2f,iterations%d".format(options.pprAlpha, options.iterations) }
}
