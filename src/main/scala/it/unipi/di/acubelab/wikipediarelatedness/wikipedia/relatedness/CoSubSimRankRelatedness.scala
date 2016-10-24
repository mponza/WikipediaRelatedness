package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.options.CoSimRankOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.cosimrank.CoSimRankPowerSeries
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraphFactory
import org.slf4j.LoggerFactory


class CoSubSimRankRelatedness(options: CoSimRankOptions) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[CoSubSimRankRelatedness])

  val csr = new CoSimRankPowerSeries(WikiGraphFactory.outGraph, options.iterations, options.pprDecay, options.csrDecay)


  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {

    val s = csr.similarity(srcWikiID, dstWikiID)
    println("%d %d %1.5f".formatLocal(java.util.Locale.US, srcWikiID, dstWikiID, s))
    s
  }

  override def toString(): String = {
    "CoSimRank_%s".format(options)
  }

}

