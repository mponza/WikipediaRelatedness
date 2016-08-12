package it.unipi.di.acubelab.graphrel.wikipedia.processing.webgraph

import it.unimi.dsi.fastutil.ints.{Int2IntArrayMap, IntArrayList, IntOpenHashSet}
import it.unimi.dsi.webgraph.ImmutableSubgraph

class WikiSubBVGraph(val superBVGraph: WikiBVGraph, val srcWikiID: Int, val dstWikiID: Int) {
  val immSubGraph = subGraph(superBVGraph, srcWikiID, dstWikiID)

  /*
  * Generate the subgraph made by a 1-level of BFS startng from srcWikiID and dstWikiID and then
  * linking the generated nodes.
  * */
  def subGraph(superBVGraph: WikiBVGraph, srcWikiID: Int, dstWikiID: Int) : ImmutableSubgraph = {
    val srcNodes = superBVGraph.successorArray(srcWikiID)
    val dstNodes = superBVGraph.successorArray(dstWikiID)

    val subgraphNodes = new IntOpenHashSet()

    subgraphNodes.add(WikiBVGraph.getNodeID(srcWikiID))
    subgraphNodes.add(WikiBVGraph.getNodeID(dstWikiID))

    subgraphNodes.addAll(new IntArrayList(srcNodes))
    subgraphNodes.addAll(new IntArrayList(dstNodes))

    new ImmutableSubgraph(superBVGraph.bvGraph, subgraphNodes)
  }

  def super2subNodeID(superNodeID: Int) : Int = {
    immSubGraph.fromSupergraphNode(superNodeID)
  }

  def getNodeID(wikiID: Int) : Int = {
    val superNodeID = WikiBVGraph.getNodeID(wikiID)
    super2subNodeID(superNodeID)
  }

  def sub2superNodeID(subNodeID: Int) : Int = {
    immSubGraph.toSupergraphNode(subNodeID)
  }

  def getWikiID(subNodeID: Int) : Int = {
    val superNodeID = sub2superNodeID(subNodeID)
    WikiBVGraph.getWikiID(superNodeID)
  }

  /**
    * @return Edges as ArrayMap (src -> dst). All elements are are wikiIDs.
    * */
  def wikiEdges() : Int2IntArrayMap = {
    val edges = new Int2IntArrayMap()

    for(i <- 0 until immSubGraph.numNodes()) {
      val wikiID = getWikiID(i)
      val succs = immSubGraph.successorArray(i).map(x => getWikiID(x))

      succs.foreach(dst => edges.put(wikiID, dst))
    }

    edges
  }
}
