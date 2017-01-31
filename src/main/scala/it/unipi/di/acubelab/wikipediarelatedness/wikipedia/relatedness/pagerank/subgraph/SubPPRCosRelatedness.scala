package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank.subgraph
/*
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory


class SubPPRCosRelatedness(options: SubPPRCosOptions = new SubPPRCosOptions()) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[CoSimRankRelatedness])


  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    val subGraph = SubWikiGraphFactory.make(options.subGraph, srcWikiID, dstWikiID, options.wikiGraphName, options.threshold)

    val wpprcos = new WeightedPPRSubCos(subGraph, options.iterations, options.pprDecay,
                                         options.weighting)

    wpprcos.similarity(srcWikiID, dstWikiID)

  }

  override def toString(): String = {
    "PPRSubCos_%s".format(options)
  }
}*/