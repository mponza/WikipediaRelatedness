package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.subgraph

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph.WikiJungSparseGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessOptions

class SubSparseRelatedness(options: RelatednessOptions) extends SubGraphRelatedness(options) {

  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = {
    val nodes = subNodeCreator.subNodes(srcWikiID, dstWikiID)
    val subGraph = new WikiJungSparseGraph(srcWikiID, dstWikiID, nodes, weighter)

    simRanker.similarity(srcWikiID, dstWikiID, subGraph).toFloat
  }
}
