package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.uncompressed

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessOptions}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory


class UncompressedJaccardIn(options: RelatednessOptions) extends Relatedness {
  val logger = LoggerFactory.getLogger(getClass)

  val wikiGraph = {
    val immInGraph = WikiBVGraphFactory.immInGraph
    val wiki2node = WikiBVGraphFactory.wiki2node
    new UncompressedWikiBVGraph(immInGraph, wiki2node)
  }

  val setOperations = new UncompressedSetOperations(wikiGraph)
  val W = wikiGraph.graph.numNodes

  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    if (srcWikiID == dstWikiID) return 1f // added

    if(!wikiGraph.wiki2node.containsKey(srcWikiID) || !wikiGraph.wiki2node.containsKey(dstWikiID)) {
      logger.warn("%d not present!" format srcWikiID )
      return 0f
    }

    if(!wikiGraph.wiki2node.containsKey(dstWikiID)) {
      logger.warn("%d not present!" format dstWikiID )
      return 0f
    }

    val intersection = setOperations.intersectionSize(srcWikiID, dstWikiID)

    if (intersection == 0) return 0f

    intersection.toFloat / setOperations.unionSize(srcWikiID, dstWikiID)
  }


  override def toString () : String = { "UncompressedJaccard_graph:in" }
}
