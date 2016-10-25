package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph

import it.unimi.dsi.fastutil.ints.{Int2IntOpenHashMap}
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.webgraph.{BVGraph, LazyIntIterator}
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import it.unimi.dsi.webgraph.ImmutableGraph
import org.slf4j.LoggerFactory


/**
  * Wrapper of BVGraph which transparently manages the mapping between wikiID and nodeID.
  */
class WikiGraph(path: String) {
  val logger = LoggerFactory.getLogger(classOf[WikiGraph])

  lazy val graph = loadImmutableGraph(path)
  protected lazy val wiki2node = BinIO.loadObject(Configuration.wikipedia("wiki2node")).asInstanceOf[Int2IntOpenHashMap]
  protected lazy val node2wiki = reverseWiki2Node()


  def loadImmutableGraph(path: String): ImmutableGraph = {
    logger.info("Loading BVGraph from %s".format(path))
    val graph = BVGraph.load(path)
    logger.info("BVGraph loaded. |Nodes| = %d and |Edges| = %d".format(graph.numNodes, graph.numArcs))

    graph
  }


  // Neighborhood Operations (wikiID -> nodeIDs)

  def successors(wikiID: Int): LazyIntIterator = {
    graph.successors(getNodeID(wikiID))
  }

  def successorArray(wikiID: Int): Array[Int] = {
    graph.successorArray(getNodeID(wikiID))
  }

  def outdegree(wikiID: Int): Int = {
    graph.outdegree(getNodeID(wikiID))
  }

  def containSuccessor(srcWikiID: Int, dstWikiID: Int): Boolean = {
    val dstNodeID = getNodeID(dstWikiID)

    val srcIter = successors(srcWikiID)
    var succ = srcIter.nextInt()

    while (succ != -1) {
      if (succ == dstNodeID) return true
      succ = srcIter.nextInt()
    }

    false
  }

  // Neighborhood Operations (nodeID -> nodeIDs)

  def nodeSuccessors(nodeID: Int): LazyIntIterator = {
    graph.successors(nodeID)
  }


  // WikiID -> NodeID mapping operations

  def contains(wikiID: Int): Boolean = {
    wiki2node.containsKey(wikiID)
  }


  def reverseWiki2Node(): Int2IntOpenHashMap = {
    val node2wiki = new Int2IntOpenHashMap
    wiki2node.keySet().toIntArray.foreach {
      wikiID: Int => node2wiki.put(wiki2node.get(wikiID), wikiID)
    }
    node2wiki
  }


  def getNodeID(wikiID: Int) : Int = {
    val nodeID = wiki2node.getOrDefault(wikiID, -1)
    if (nodeID < 0) {
      throw new IllegalArgumentException("WikiID %d not present in the Wikipedia graph.".format(wikiID))
    }
    nodeID
  }


  def getWikiID(nodeID: Int): Int = {
    val wikiID = node2wiki.getOrDefault(nodeID, -1)
    if (wikiID < 0) {
      throw new IllegalArgumentException("NodeIndex %d not present in the Wikipedia mapping.".format(nodeID))
    }
    wikiID
  }


}