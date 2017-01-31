package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank.subgraph.context
/*
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms.JungCoSimRank
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.oldgraph.JungCliqueWikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessOptions}
import org.slf4j.LoggerFactory


class ContextCliqueCoSimRankRelatedness(options: RelatednessOptions) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[ContextCliqueCoSimRankRelatedness])

  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    // WebGraph subgraph
    val wgSubGraph = SubWikiGraphFactory.make(options.subGraph, srcWikiID, dstWikiID, options.graph, options.threshold)
    // Jung subgraph
    val subGraph = new JungCliqueWikiGraph(wgSubGraph)
    //  Jung CoSimRank
    val jungCSR = new JungCoSimRank(subGraph, options.weights, options.iterations, options.pprDecay, options.csrDecay)

    jungCSR.similarity(srcWikiID, dstWikiID)
  }


  override def toString(): String = {
    "ContextCliqueCoSimRankRelatedness_%s".format(options)
  }
}



class JungCliqueCoSimRankRelatedness(options: SubCoSimRankOptions = new SubCoSimRankOptions()) extends Relatedness {

  val logger = LoggerFactory.getLogger(classOf[JungCliqueCoSimRankRelatedness])

  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    // WebGraph subgraph
    val wgSubGraph = SubWikiGraphFactory.make(options.subGraph, srcWikiID, dstWikiID, options.wikiGraphName, options.threshold)
    // Jung subgraph
    val subGraph = new JungCliqueWikiGraph(wgSubGraph)
    //  Jung CoSimRank
    val jungCSR = new JungCoSimRank(subGraph, options.weighting, options.iterations, options.pprDecay, options.csrDecay)

    jungCSR.similarity(srcWikiID, dstWikiID)
  }

  override def toString(): String = {
    "JungCliqueCoSimRank_%s".format(options)
  }
}

  */