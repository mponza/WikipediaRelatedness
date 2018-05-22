package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.graphbased

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.graph.WikiGraph

class MilneWittenRelatedness(wikiGraph: WikiGraph) extends WikiGraphBasedRelatedness(wikiGraph) {

  override def relatedness(srcWikiID: Int, dstWikiID: Int): Float = {

    val intersectionSize = super.intersection(srcWikiID, dstWikiID)
    if(intersectionSize == 0) return 0f

    val srcOut = wikiGraph.degree(srcWikiID)
    val dstOut = wikiGraph.degree(dstWikiID)

    val n = wikiGraph.getNumNodes

    val rel = (math.log(srcOut max dstOut) - math.log(intersectionSize) ) /
               (math.log(n) - math.log(srcOut min dstOut))

    val normRel = 1f - ((rel.toFloat max 0f) min 1f)
    normRel
  }
}
