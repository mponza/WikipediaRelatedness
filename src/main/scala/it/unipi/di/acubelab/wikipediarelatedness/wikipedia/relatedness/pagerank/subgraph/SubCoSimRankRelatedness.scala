package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank.subgraph

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory


/*
class SubCoSimRankRelatedness(options: SubCoSimRankOptions = new SubCoSimRankOptions()) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[SubCoSimRankRelatedness])


  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    val subGraph = SubWikiGraphFactory.make(options.subGraph, srcWikiID, dstWikiID,
                                             options.wikiGraphName, options.threshold)

    val wcsr = new WeightedCoSubSimRank(subGraph, options.iterations, options.pprDecay,
                                         options.csrDecay, options.weighting)

    wcsr.similarity(srcWikiID, dstWikiID)
  }

  override def toString(): String = {
    "SubCoSimRank_%s".format(options)
  }

}*/