package it.unipi.di.acubelab.graphrel.wikipedia.processing.webgraph

import it.unimi.dsi.fastutil.ints.{Int2IntOpenHashMap, Int2ObjectOpenHashMap, IntArrayList, IntOpenHashSet}
import it.unimi.dsi.webgraph.ImmutableSubgraph
import org.slf4j.LoggerFactory


class WikiSubBVGraph(val superBVGraph: WikiBVGraph, val srcWikiID: Int, val dstWikiID: Int) {
  val logger = LoggerFactory.getLogger(classOf[WikiSubBVGraph])
  val immSubGraph = subGraph(superBVGraph, srcWikiID, dstWikiID)

  /*
  * Generate the subgraph made by a 1-level of BFS startng from srcWikiID and dstWikiID and then
  * linking the generated nodes.
  * */
  def subGraph(superBVGraph: WikiBVGraph, srcWikiID: Int, dstWikiID: Int) : ImmutableSubgraph = {
    logger.info("Generating subgraph starting from %d and %d...".format(srcWikiID, dstWikiID))

    val srcNodes = superBVGraph.successorArray(srcWikiID)
    val dstNodes = superBVGraph.successorArray(dstWikiID)

    val subgraphNodes = new IntOpenHashSet()

    subgraphNodes.add(WikiBVGraph.getNodeID(srcWikiID))
    subgraphNodes.add(WikiBVGraph.getNodeID(dstWikiID))

    subgraphNodes.addAll(new IntArrayList(srcNodes))
    subgraphNodes.addAll(new IntArrayList(dstNodes))

    val subGraph = new ImmutableSubgraph(superBVGraph.bvGraph, subgraphNodes)

    logger.info("Subgraph generated with %d nodes.".format(subGraph.numNodes))

    subGraph
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

  def outdegree(wikiID: Int) : Int = {
    immSubGraph.outdegree(getNodeID(wikiID))
  }

  /**
    * @return Edges as ArrayMap (src -> [dst]). All elements are are wikiIDs.
    * */
  def wikiEdges() : Int2ObjectOpenHashMap[IntArrayList] = {
    val edges = new Int2ObjectOpenHashMap[IntArrayList]

    for(i <- 0 until immSubGraph.numNodes()) {
      val wikiID = getWikiID(i)
      val succs = immSubGraph.successorArray(i).map(x => getWikiID(x))

      succs.foreach {
        dst => edges.putIfAbsent(wikiID, new IntArrayList)
               edges.get(wikiID).add(dst)
      }
    }

    edges
  }
}
