package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.subgraph

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory

class NeighSubWikiBVGraph(srcWikiID: Int, dstWikiID: Int, wikiGraph: WikiBVGraph)
  extends SubWikiBVGraph(srcWikiID, dstWikiID, wikiGraph) {

  override def neighborhood(srcWikiID: Int) : Array[Int] = {
    List(WikiBVGraphFactory.outWikiBVGraph,
      WikiBVGraphFactory.inWikiBVGraph)
        .map(
            bvGraph => bvGraph.successorArray(srcWikiID)
          ).toArray.flatten.distinct
  }

}
