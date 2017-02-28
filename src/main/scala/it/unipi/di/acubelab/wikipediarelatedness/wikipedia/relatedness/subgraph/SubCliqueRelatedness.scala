package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.subgraph

import java.util.Locale

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph.WikiJungCliqueGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessOptions


/**
  * Relatedness method that uses subNodeCreator to generate a cliqued Wikipedia subgraph
  * weighted with the weighter relatedness.
  *
  * @param options
  */
class SubCliqueRelatedness(options: RelatednessOptions) extends SubGraphRelatedness(options) {


  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = {

    val nodes = subNodeCreator.subNodes(srcWikiID, dstWikiID)
    val subGraph = new WikiJungCliqueGraph(nodes, weighter)

    simRanker.similarity(srcWikiID, dstWikiID, subGraph).toFloat
  }

  override def toString() = "Clique_%s".format(subGraphString())

}