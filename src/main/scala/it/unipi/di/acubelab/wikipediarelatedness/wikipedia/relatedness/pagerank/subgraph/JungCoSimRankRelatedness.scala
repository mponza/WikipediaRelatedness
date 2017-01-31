package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank.subgraph
/*
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.oldgraph.JungDirectedSubWikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms.JungCoSimRank
import org.slf4j.LoggerFactory


class JungCoSimRankRelatedness(options: SubCoSimRankOptions = new SubCoSimRankOptions())  extends Relatedness {

 val logger = LoggerFactory.getLogger(classOf[JungCoSimRankRelatedness])

  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    // WebGraph subgraph
    val wgSubGraph = SubWikiGraphFactory.make(options.subGraph, srcWikiID, dstWikiID, options.wikiGraphName, options.threshold)
    // Jung subgraph
    val subGraph = new JungDirectedSubWikiGraph(wgSubGraph)
    //  Jung CoSimRank
    val jungCSR = new JungCoSimRank(subGraph, options.weighting, options.iterations, options.pprDecay, options.csrDecay)

    jungCSR.similarity(srcWikiID, dstWikiID)
  }


  override def toString(): String = {
    "JungCoSimRank_%s".format(options)
  }
}*/