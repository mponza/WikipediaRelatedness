package it.unipi.di.acubelab.graphrel.wikipedia.processing.webgraph

import it.unimi.dsi.webgraph.algo.{GeometricCentralities, ParallelBreadthFirstVisit}
import it.unimi.dsi.fastutil.ints.{Int2IntOpenHashMap, IntArrayList, IntOpenHashSet}
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.law.rank.PageRankParallelGaussSeidel
import it.unimi.dsi.webgraph.{BVGraph, LazyIntIterator, Transform}
import it.unipi.di.acubelab.graphrel.utils.Configuration
import org.slf4j.LoggerFactory


/**
  * Wrapper of BVGraph which transparently manages the mapping between wikiID and nodeID.
  */
class WikiBVGraph(path: String) {
  val logger = LoggerFactory.getLogger(classOf[WikiBVGraph])
  val bvGraph = loadBVGraph(path)

  protected lazy val pageRanks = computeCentrality("PageRank")
  protected lazy val harmonicRanks = computeCentrality("Harmonic")

  // (srcWikiID, dstWikiID) => distance in the bvGraph
  val distanceCache = scala.collection.mutable.HashMap.empty[Tuple2[Int, Int], Int]

  def loadBVGraph(path: String) = {
    logger.info("Loading BVGraph from %s".format(path))
    val graph = BVGraph.load(path)
    logger.info("BVGraph loaded. |Nodes| = %d and |Edges| = %d".format(graph.numNodes, graph.numArcs))
    graph
  }

  def successors(wikiID: Int) : LazyIntIterator = {
    bvGraph.successors(WikiBVGraph.getNodeID(wikiID))
  }

  def successorArray(wikiID: Int) : Array[Int] = {
    bvGraph.successorArray(WikiBVGraph.getNodeID(wikiID))
  }

  def outdegree(wikiID: Int) : Int = {
    bvGraph.outdegree(WikiBVGraph.getNodeID(wikiID))
  }

  /**
    * @param srcWikiID
    * @param dstWikiID
    * @return True if the successor list of srcWikiID contains dstWikiID.
    */
  def containSuccessor(srcWikiID: Int, dstWikiID: Int) : Boolean = {
    val dstNodeID = WikiBVGraph.getNodeID(dstWikiID)

    val srcIter = successors(srcWikiID)
    var succ = srcIter.nextInt()

    while(succ != -1) {
      if(succ == dstNodeID) return true
      succ = srcIter.nextInt()
    }

    false
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

  /**
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return wikiIDs which belong to the union between srcWikiID and dstWikiID.
    */
  def nodeUnion(srcWikiID: Int, dstWikiID: Int) : IntArrayList = {
    val srcArray = successorArray(srcWikiID)
    val dstArray = successorArray(dstWikiID)

    new IntArrayList((srcArray ++ dstArray).distinct.map(nodeID => WikiBVGraph.getWikiID(nodeID)))
  }

  /**
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return wikiIDs which belong to the intersection between srcWikiID and dstWikiID.
    */
  def nodeIntersection(srcWikiID: Int, dstWikiID: Int) : IntArrayList = {

    val iterA =  successors(srcWikiID)
    val iterB = successors(dstWikiID)

    var intersection = new IntArrayList
    var a = iterA.nextInt
    var b = iterB.nextInt

    do {
      if (a == b) {
        intersection.add(WikiBVGraph.getWikiID(a))
        a = iterA.nextInt
        b = iterB.nextInt
      }

      // Aligns iterators to their minimum common element (if any).
      while (a < b && a != -1) a = iterA.nextInt
      while (b < a && b != -1) b = iterB.nextInt

    } while(a != -1 && b != -1)

    intersection
  }

  /**
    * @return Distance in bvGraph from srcWikiID to dstWikiID. -1 if infinity distance.
    */
  def distance(srcWikiID: Int, dstWikiID: Int) : Int = {

    if (!distanceCache.contains((srcWikiID, dstWikiID))) {

      val distance = bfsDistance(srcWikiID, dstWikiID)
      distanceCache.put((srcWikiID, dstWikiID), distance)
    }

    distanceCache((srcWikiID, dstWikiID))
  }


  def bfsDistance(srcWikiID: Int, dstWikiID: Int) : Int = {
    if (srcWikiID == dstWikiID) return 0

    val bfs = new ParallelBreadthFirstVisit(bvGraph, 0, false, null)

    bfs.visit(WikiBVGraph.getNodeID(srcWikiID))

    for(d <- 1 until bfs.cutPoints.size - 1) {

      // Get nodes visited at d-th iteration of BFS.
      val dIndex = bfs.cutPoints.getInt(d)
      val dPlusOneIndex = bfs.cutPoints.getInt(d + 1)
      val dNodes = bfs.queue.subList(dIndex, dPlusOneIndex)

      if(dNodes.contains(WikiBVGraph.getNodeID(dstWikiID))) return d
    }

    Int.MaxValue
  }

  def computeCentrality(centrality: String = "PageRank", iterations: Int = 100) : Array[Double] = centrality match {
    case "PageRank" =>
      val pageRanker = new PageRankParallelGaussSeidel(Transform.transpose(bvGraph))

      logger.info("Computing PageRank with %d iterations...".format(iterations))
      for(i <- 0 to iterations) {
        pageRanker.step()
      }

      pageRanker.rank

    case "Harmonic" =>
      val harmonicRanker = new GeometricCentralities(bvGraph)

      logger.info("Computing Harmonic Centrality...")
      harmonicRanker.harmonic

    case _ => throw new IllegalArgumentException("%s Centrality not supported.".format(centrality))
  }

  def pageRankScore(wikiID: Int): Double = {
    pageRanks(WikiBVGraph.getNodeID(wikiID))
  }

  def harmonicScore(wikiID: Int): Double = {
    harmonicRanks(WikiBVGraph.getNodeID(wikiID))
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

  def getWikiID(nodeID: Int) : Int = {
    val wikiID = node2wiki.getOrDefault(nodeID, -1)
    if (wikiID < 0) {
      throw new IllegalArgumentException("NodeIndex %d not present in the Wikipedia mapping.".format(nodeID))
    }
    wikiID
  }
}
