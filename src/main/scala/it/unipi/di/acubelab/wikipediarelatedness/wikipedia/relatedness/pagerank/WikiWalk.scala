package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank

import org.slf4j.LoggerFactory

class WikiWalkRelatedness(options: WikiWalkOptions) extends Relatedneess {
  val logger = LoggerFactory.getLogger(classOf[WikiWalkRelatedness])


  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    val subGraph = SubWikiGraphFactory.make(options.subGraph, srcWikiID, dstWikiID)

    val wpprcos = new WeightedPPRSubCos(subGraph, options.iterations, options.pprDecay,
      options.weighting)

    wpprcos.similarity(srcWikiID, dstWikiID)

  }


}
