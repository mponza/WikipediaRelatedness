package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.{WikiBVGraph, WikiBVGraphFactory}


object WikiJungGraphFactory {

  /**
    * Makes a WikiJungGraph out-directed and uniformly weighted.
    *
    * @return
    */
  def make() = {
    val wikiBVgraph = WikiBVGraphFactory.makeWikiBVGraph("out")
    val edges = wikiBVGraph2Edges(wikiBVgraph)
    new WikiJungGraph(edges)
  }


  /**
    * Transforms a wikiBVGraph in a flat array of node pairs. Each pair is a directed edge.
    *
    * @param wikiBVgraph
    */
  protected def wikiBVGraph2Edges(wikiBVgraph: WikiBVGraph) = {
    val edges = new ObjectArrayList[(Int, Int)]()

    for ( srcWikiID <- wikiBVgraph.getWikiIDs ) {
      for ( dstWikiID <- wikiBVgraph.wikiSuccessors(srcWikiID) ) {
        edges.add((srcWikiID, dstWikiID))
      }
    }

    edges.elements()
  }

}
