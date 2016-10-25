package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank

import it.unipi.di.acubelab.wikipediarelatedness.options.CoSubSimRankOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.SubWikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.pagerank.cosimrank.WeightedCoSubSimRank
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory


class CoSubSimRankRelatedness(options: CoSubSimRankOptions) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[CoSubSimRankRelatedness])

  // /new CoSimRankPowerSeries(WikiGraphFactory.outGraph, options.iterations, options.pprDecay, options.csrDecay)


  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    val subGraph = new SubWikiGraph(srcWikiID, dstWikiID)
    val wcsr = new WeightedCoSubSimRank(subGraph, options.iterations, options.pprDecay,
                                         options.csrDecay, options.weighting)

    val s = wcsr.similarity(srcWikiID, dstWikiID)
    println("%d %d %1.5f".formatLocal(java.util.Locale.US, srcWikiID, dstWikiID, s))
    s
  }

  override def toString(): String = {
    "CoSimRank_%s".format(options)
  }

}

