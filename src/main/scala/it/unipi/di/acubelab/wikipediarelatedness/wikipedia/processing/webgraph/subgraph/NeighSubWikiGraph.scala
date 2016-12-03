package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.subgraph

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraphFactory

class NeighSubWikiGraph(srcWikiID: Int, dstWikiID: Int, wikiGraph: WikiGraph)
  extends SubWikiGraph(srcWikiID, dstWikiID, wikiGraph) {

  override def neighborhood(srcWikiID: Int) : Array[Int] = {
    List(WikiGraphFactory.outGraph,
      WikiGraphFactory.inGraph)
        .map(
            bvGraph => bvGraph.successorArray(srcWikiID)
          ).toArray.flatten.distinct
  }

}
