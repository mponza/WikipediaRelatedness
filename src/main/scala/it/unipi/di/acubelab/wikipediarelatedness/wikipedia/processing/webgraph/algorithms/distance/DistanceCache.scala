package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.distance


import it.unimi.dsi.fastutil.ints.{Int2ObjectOpenHashMap, IntArrayList}
import it.unimi.dsi.webgraph.algo.ParallelBreadthFirstVisit

class DistanceCache {
  val visits = new Int2ObjectOpenHashMap[Tuple2[IntArrayList, IntArrayList]]()  // {nodeID -> (nodeIDs, cutpoints)}


  /**
    *
    * @param srcNodeID
    * @param dstNodeID
    * @return null; if srcNodeID never visited, -1 if srcNodeID cannot reach dstNodeID, the BFS distance otherwise.
    */
  def getDistance(srcNodeID: Int, dstNodeID: Int) : Int = {
    // BFS of srcNodeID never computed before in the graph.
    if (!visits.containsKey(srcNodeID))  return null

    // src has been already visited.
    val (queue,  cutPoints) = visits.get(srcNodeID)
    getDistance(queue, cutPoints, dstNodeID)

  }



  protected def getDistance(queue: IntArrayList, cutPoints: IntArrayList, nodeID: Int) : Int = {
    for(distance <- 1 until cutPoints.size() - 1) {
      val cutIndex = cutPoints.getInt(distance)
      val cutIndexPlusOne = cutPoints.getInt(distance + 1)

      val nodes = queue.subList(cutIndex, cutIndexPlusOne)
      if (nodes.contains(nodeID)) return distance
    }

    return -1
  }


  /**
    * Updates visits and return the distance between srcNodeID and dstNodeID.
    * @param srcNodeID
    * @param dstNodeID
    * @param bfs
    * @return The distance between srcNodeID and dstNodeID.
    */
  def updateCacheNgetDistance(srcNodeID: Int, dstNodeID: Int, bfs: ParallelBreadthFirstVisit) : Int = {
    visits.putIfAbsent(srcNodeID, (bfs.queue, bfs.cutPoints))
    getDistance(srcNodeID, dstNodeID)
  }
}