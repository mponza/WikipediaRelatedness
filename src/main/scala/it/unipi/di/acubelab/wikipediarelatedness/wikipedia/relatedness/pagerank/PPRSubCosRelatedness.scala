package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank

import it.unipi.di.acubelab.wikipediarelatedness.options.PPRSubCosOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.pagerank.pprcos.WeightedPPRSubCos
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraphFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.subgraph.SubWikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory

class PPRSubCosRelatedness(options: PPRSubCosOptions) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[CoSimRankRelatedness])


  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    val subGraph = new SubWikiGraph(srcWikiID, dstWikiID)


    val wpprcos = new WeightedPPRSubCos(WikiGraphFactory.outGraph, options.iterations, options.pprDecay,
                                         options.weighting)

    wpprcos.similarity(srcWikiID, dstWikiID)

  }

  override def toString(): String = {
    "PPRSubCos_%s".format(options)
  }
}


/**
  * def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    val subGraph = new SubWikiGraph(srcWikiID, dstWikiID)

    val wcsr = new WeightedCoSubSimRank(subGraph, options.iterations, options.pprDecay,
                                         options.csrDecay, options.weighting)

    wcsr.similarity(srcWikiID, dstWikiID)
  }

  override def toString(): String = {
    "CoSimRank_%s".format(options)
  }
  */