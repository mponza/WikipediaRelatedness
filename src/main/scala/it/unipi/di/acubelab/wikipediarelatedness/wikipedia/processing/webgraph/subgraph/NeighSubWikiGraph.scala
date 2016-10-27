package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.subgraph

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph

class NeighSubWikiGraph(srcWikiID: Int, dstWikiID: Int) extends SubWikiGraph(srcWikiID, dstWikiID) {

  override def neighborhood(srcWikiID: Int) : Array[Int] = {
    List(outGraph, inGraph).map(bvGraph => bvGraph.successorArray(srcWikiID)).toArray.flatten.distinct
  }

}
