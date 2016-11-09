package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.distance

import it.unimi.dsi.webgraph.algo.ParallelBreadthFirstVisit
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraph

/**
  * Class which allows to compute multiple distance from a source to a list of nodes (see computeDistances function)
  * @param wikiGraph
  */
class MultipleDistanceMeter(wikiGraph: WikiGraph) extends DistanceMeter(wikiGraph) {

  /**
    *
    * @param srcWikiID
    * @param dstWikiIDs
    * @return dstNodeIDs mapped into the corresponding distance.
    */
  def computeDistances(srcWikiID: Int, dstWikiIDs: List[Int]) : List[Int] = {
    val bfs = computeBFS(wikiGraph.getNodeID(srcWikiID))
    val distances = distanceMap(bfs)

    dstWikiIDs.map(wikiID => distances.getOrDefault(wikiGraph.getNodeID(wikiID), -1).toInt).toList
  }

  /**
    *
    * @param bfs
    * @return Hash of {nodeID -> distance}
    */
  def distanceMap(bfs: ParallelBreadthFirstVisit) : Int2IntOpenHashMap = {
    val distances = new Int2IntOpenHashMap()

    for(distance <- 1 until bfs.cutPoints.size()- 1) {

      val cutIndex = bfs.cutPoints.getInt(distance)
      val cutIndexPlusOne = bfs.cutPoints.getInt(distance + 1)

      for(nodeID <- bfs.queue.subList(cutIndex, cutIndexPlusOne).toIntArray()) {
        distances.putIfAbsent(nodeID, distance)
      }
    }

    distances
  }
}
