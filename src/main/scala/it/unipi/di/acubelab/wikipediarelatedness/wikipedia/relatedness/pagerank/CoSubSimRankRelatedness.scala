package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank

import it.unipi.di.acubelab.wikipediarelatedness.options.CoSubSimRankOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.pagerank.cosimrank.WeightedCoSubSimRank
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.subgraph.{SubWikiGraph, SubWikiGraphFactory}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory


class CoSubSimRankRelatedness(options: CoSubSimRankOptions = new CoSubSimRankOptions()) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[CoSubSimRankRelatedness])


  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    val subGraph = SubWikiGraphFactory.make(options.subGraph, srcWikiID, dstWikiID)

    val wcsr = new WeightedCoSubSimRank(subGraph, options.iterations, options.pprDecay,
                                         options.csrDecay, options.weighting)

    wcsr.similarity(srcWikiID, dstWikiID)
  }

  override def toString(): String = {
    "CoSubSimRank_%s".format(options)
  }

}

