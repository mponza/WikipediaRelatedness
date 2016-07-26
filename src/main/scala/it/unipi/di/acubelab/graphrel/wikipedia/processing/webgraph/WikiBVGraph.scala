package it.unipi.di.acubelab.graphrel.wikipedia.processing.webgraph

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.webgraph.{BVGraph, LazyIntIterator}
import it.unipi.di.acubelab.graphrel.utils.Configuration
import org.slf4j.LoggerFactory


/**
  * Wrapper of BVGraph which transparently manages the mapping between wikiID and nodeID.
  */
class WikiBVGraph(path: String) {
  val logger = LoggerFactory.getLogger(classOf[WikiBVGraph])
  val bvGraph = loadBVGraph(path)

  def loadBVGraph(path: String) = {
    logger.info("Loading BVGraph from %s".format(path))
    val graph = BVGraph.load(path)
    logger.info("BVGraph loaded. |Nodes| = %d and |Edges| = %d".format(graph.numNodes, graph.numArcs))
    graph
  }

  def successors(wikiID: Int) : LazyIntIterator = {
    bvGraph.successors(WikiBVGraph.getNodeID(wikiID))
  }

  def outdegree(wikiID: Int) : Int = {
    bvGraph.outdegree(WikiBVGraph.getNodeID(wikiID))
  }

  def linkIntersection(srcWikiID: Int, dstWikiID: Int) : Int = {
    val iterA =  successors(srcWikiID)
    val iterB = successors(dstWikiID)

    var intersection = 0
    var a = iterA.nextInt
    var b = iterB.nextInt

    do {
      if (a == b) {
        intersection += 1
        a = iterA.nextInt
        b = iterB.nextInt
      }

      // Aligns iterators to their minimum common element (if any).
      while (a < b && a != -1) a = iterA.nextInt
      while (b < a && b != -1) b = iterB.nextInt

    } while(a != -1 && b != -1)

    intersection
  }
}

object WikiBVGraph {
  lazy val wiki2node = BinIO.loadObject(Configuration.wikipedia("wiki2node")).asInstanceOf[Int2IntOpenHashMap]
  lazy val node2wiki = reverseWiki2Node()

  def contains(wikiID: Int): Boolean = {
    wiki2node.containsKey(wikiID)
  }

  def reverseWiki2Node() : Int2IntOpenHashMap = {
    val node2wiki = new Int2IntOpenHashMap
    wiki2node.keySet().toIntArray.foreach {
      wikiID : Int => node2wiki.put(wiki2node.get(wikiID), wikiID)
    }
    node2wiki
  }

  def getNodeID(wikiID: Int) = {
    val nodeID = wiki2node.getOrDefault(wikiID, -1)
    if (nodeID < 0) {
      throw new IllegalArgumentException("WikiID %d not present in the Wikipedia graph.".format(wikiID))
    }
    nodeID
  }

  def getWikiID(index: Int) : Int = {
    val wikiID = node2wiki.getOrDefault(index, -1)
    if (wikiID < 0) {
      throw new IllegalArgumentException("NodeIndex %d not present in the Wikipedia mapping.".format(index))
    }
    wikiID
  }
}
