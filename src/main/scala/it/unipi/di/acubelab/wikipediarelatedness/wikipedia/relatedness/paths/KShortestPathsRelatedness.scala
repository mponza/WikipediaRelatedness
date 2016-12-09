package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.paths

import it.unipi.di.acubelab.wikipediarelatedness.options.PPRCosOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.subgraph.SubWikiGraphFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory

class KShortestPathsRelatedness(options: KShortestPathsOption) extends Relatedness {

  val logger = LoggerFactory.getLogger(classOf[KShortestPathsRelatedness])

  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    // WebGraph subgraph
    val wgSubGraph = SubWikiGraphFactory.make(options.subGraph, srcWikiID, dstWikiID,, options.threshold)

    val subGraph = new JungCliqueWikiGraph(wgSubGraph)
  }

}




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