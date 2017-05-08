package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms

import it.unimi.dsi.fastutil.ints.{Int2ObjectOpenHashMap, IntArrayList}
import it.unimi.dsi.webgraph.algo.ParallelBreadthFirstVisit
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping.WikiTitleID
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.{WikiBVGraph, WikiBVGraphFactory}
import org.slf4j.LoggerFactory

/**
  * Class used to compute distance between two Wikipedia IDs in the whole Wikipedia graph.
  *
  * @param wikiGraph
  */
class WikiBVDistance(wikiGraph: WikiBVGraph = WikiBVGraphFactory.make("un.out") ) { // WikiBVGraphFactory.make("sym")) {
  protected val logger = LoggerFactory.getLogger(getClass)
  protected val visits = new Int2ObjectOpenHashMap[Tuple2[IntArrayList, IntArrayList]]()  // {nodeID -> (nodeIDs, cutpoints)}


  /**
    * Returns distance between two Wikipedia nodes in the Wikipedia graph.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  def getDistance(srcWikiID: Int, dstWikiID: Int) : Int = {
    if (srcWikiID == dstWikiID) return 0

    val graph = wikiGraph.graph

    val srcNodeID = wikiGraph.getNodeID(srcWikiID)
    val dstNodeID = wikiGraph.getNodeID(dstWikiID)

    if (graph.successorArray(srcNodeID).contains(dstNodeID)) {
      logger.debug("Distance between %s and %s is 1.".format(WikiTitleID.map(srcWikiID), WikiTitleID.map(dstWikiID)))
      return 1
    }

    val dist = try {

      getBFSDistance(srcNodeID, dstNodeID)

    } catch {

      case e: IllegalArgumentException =>
        // New distance computation
        val bfs = computeBFS(srcNodeID)
        updateVisits(srcNodeID, dstNodeID, bfs)
    }

    logger.debug("Distance between %s and %s is %d.".format(WikiTitleID.map(srcWikiID), WikiTitleID.map(dstWikiID), dist))
    dist
  }


  //
  // Methods that uses visit and possibly perform the BFS visit.

  /**
    *
    * @param srcNodeID
    * @param dstNodeID
    * @return null; if srcNodeID never visited, -1 if srcNodeID cannot reach dstNodeID, the BFS distance otherwise.
    */
  protected def getBFSDistance(srcNodeID: Int, dstNodeID: Int) : Int = {
    // BFS of srcNodeID never computed before in the graph.
    if (!visits.containsKey(srcNodeID)) throw new IllegalArgumentException("Src node not yet visited.")

    // src has been already visited.
    val (queue,  cutPoints) = visits.get(srcNodeID)
    getDistance(queue, cutPoints, dstNodeID)
  }


  protected def computeBFS(srcNodeID: Int) : ParallelBreadthFirstVisit = {
    val maxDistance = 10

    val bfs = new ParallelBreadthFirstVisit(wikiGraph.graph, 0, false, null)
    for (i <- 0 until maxDistance) bfs.visit(srcNodeID)

    bfs
  }



  protected def getDistance(queue: IntArrayList, cutPoints: IntArrayList, nodeID: Int) : Int = {
    for(distance <- 1 until cutPoints.size() - 1) {
      val cutIndex = cutPoints.getInt(distance)
      val cutIndexPlusOne = cutPoints.getInt(distance + 1)

      val nodes = queue.subList(cutIndex, cutIndexPlusOne)
      if (nodes.contains(nodeID)) return distance
    }

    -1
  }


  /**
    * Updates visits and return the distance between srcNodeID and dstNodeID.
    *
    * @param srcNodeID
    * @param dstNodeID
    * @param bfs
    * @return The distance between srcNodeID and dstNodeID.
    */
  protected def updateVisits(srcNodeID: Int, dstNodeID: Int, bfs: ParallelBreadthFirstVisit) : Int = {
    visits.putIfAbsent(srcNodeID, (bfs.queue, bfs.cutPoints))
    getBFSDistance(srcNodeID, dstNodeID)
  }

}
