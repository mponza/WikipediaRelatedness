package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.firststage.nodes

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.graph.WikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.WikiRelatedness

/**
  * Implements the first stage where top-k most related nodes of srcWikiID and dstWikiID are
  * the one in the neighborhood of srcWikiID and dstWikiID in wikiGraph, weighted with a wikiRel function.
  *
  */
class RelatedWikiNeighbourNodesOfSubGraph(wikiGraph: WikiGraph, wikiRelatedness: WikiRelatedness)
       extends NodesOfSubGraph {

  def this(wikiGraphFilename: String, wikiRelatedness: WikiRelatedness) = {
    this(WikiGraph(wikiGraphFilename), wikiRelatedness)
  }

  /**
    * Sort neighbors of wikiID with respect to their relatedness (high relatedness first).
    *
    * @param wikiID
    */
  override def topNodes(wikiID: Int): Seq[Int] = {
    val edges = wikiGraph.edges(wikiID)
    val scoredEdges = edges.toIntArray.map( edgeWikiID => (edgeWikiID, wikiRelatedness.relatedness(wikiID, edgeWikiID)))
    scoredEdges.sortBy(- _._2).map(_._1)
  }


  def getWikiGraph : WikiGraph = { wikiGraph }
  def getWikiRelatedness : WikiRelatedness = { wikiRelatedness }

}
