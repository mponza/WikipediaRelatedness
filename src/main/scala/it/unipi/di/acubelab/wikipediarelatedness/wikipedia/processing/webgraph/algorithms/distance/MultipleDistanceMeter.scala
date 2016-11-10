package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.distance

import it.unimi.dsi.webgraph.algo.ParallelBreadthFirstVisit
import it.unimi.dsi.fastutil.ints.{Int2IntOpenHashMap, IntArrayList}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraph
import org.slf4j.LoggerFactory

/**
  * Class which allows to compute multiple distance from a source to a list of nodes (see computeDistances function)
  *
  * @param wikiGraph
  */
class MultipleDistanceMeter(wikiGraph: WikiGraph) {
  val logger = LoggerFactory.getLogger(classOf[MultipleDistanceMeter])

  val maxDistance = 3

  /**
    *
    * @param srcWikiID
    * @param dstWikiIDs
    * @return dstNodeIDs mapped into the corresponding distance.
    */
  def getDistances(srcWikiID: Int, dstWikiIDs: List[Int]) : Array[Int] = {
    val srcNodeID = wikiGraph.getNodeID(srcWikiID)

    val bfs = new ParallelBreadthFirstVisit(wikiGraph.graph, 0, false, null)
    for (i <- 0 until maxDistance) bfs.visit(srcNodeID)

    val dstNodeIDs = dstWikiIDs.map(wikiID => wikiGraph.getNodeID(wikiID))
    val nodeID2index = dstNodeIDs.zipWithIndex.toMap
    val distances = Array.fill(dstNodeIDs.size)(-1)

    var i = 0
    var n = 0
    for(nodeID <- bfs.queue.toIntArray()) {

      if (nodeID2index.contains(nodeID)) {
        val dstIndex = nodeID2index(nodeID)
        distances(dstIndex) = index2Distance(bfs.cutPoints, i)
        n += 1
      }

      if (n == distances.size) return distances

      i += 1
    }

    distances
  }


  protected def index2Distance(cutPoints: IntArrayList, index: Int) : Int = {
    for(distance <- 1 until cutPoints.size() - 1) {
      val lower = cutPoints.getInt(distance)
      val greater = cutPoints.getInt(distance + 1)

      if(lower <= index && index < greater) return distance
    }

    -1
  }

}
