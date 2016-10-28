package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank

import it.unipi.di.acubelab.wikipediarelatedness.options.WikiWalkOptions
import org.slf4j.LoggerFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.pagerank.wikiwalk.WikiWalk
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness


class WikiWalkRelatedness(options: WikiWalkOptions)  extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[WikiWalkRelatedness])


  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {

    val wpprcos = new WikiWalk()

    wpprcos.similarity(srcWikiID, dstWikiID)

  }

}