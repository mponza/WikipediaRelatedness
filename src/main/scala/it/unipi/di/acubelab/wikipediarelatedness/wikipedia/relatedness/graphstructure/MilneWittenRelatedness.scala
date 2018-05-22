package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.graphstructure

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.graph.WikiGraph
import org.slf4j.LoggerFactory

class MilneWittenRelatedness(wikiGraph: WikiGraph) extends WikiGraphBasedRelatedness(wikiGraph) {

  private val logger = LoggerFactory.getLogger(classOf[MilneWittenRelatedness])

  override def relatedness(srcWikiID: Int, dstWikiID: Int): Float = {

    val intersectionSize = super.intersection(srcWikiID, dstWikiID)
    logger.info(intersectionSize.toString)
    if(intersectionSize == 0) return 0f

    val srcDegree = wikiGraph.degree(srcWikiID)
    val dstDegree = wikiGraph.degree(dstWikiID)

    val n = wikiGraph.getNumNodes

    val rel = (math.log(srcDegree max dstDegree) - math.log(intersectionSize) ) /
               (math.log(n) - math.log(srcDegree min dstDegree))

    val normRel = 1f - ((rel.toFloat max 0f) min 1f)

    normRel
  }

  override def name() = "Milne&WittenRelatedness"
}
