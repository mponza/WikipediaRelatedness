package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.distance

import it.unimi.dsi.webgraph.algo.{ParallelBreadthFirstVisit, StronglyConnectedComponents}
import it.unimi.dsi.fastutil.ints.{Int2IntOpenHashMap, IntArrayList}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraph
import org.slf4j.LoggerFactory

/**
  * Class which allows to compute multiple distance from a source to a list of nodes (see computeDistances function)
  * without using space-consuming cache.
  *
  * @param wikiGraph
  */
class MultipleDistanceMeter(wikiGraph: WikiGraph) {
  val logger = LoggerFactory.getLogger(classOf[MultipleDistanceMeter])

  logger.info("Computing SCC...")
  val nodeID2Component = StronglyConnectedComponents.compute(wikiGraph.graph, false, null).component

  /**
    *
    * @param srcWikiID
    * @param dstWikiIDs
    * @return dstNodeIDs mapped into the corresponding distance.
    */
  def getDistances(srcWikiID: Int, dstWikiIDs: List[Int]) : Array[Int] = {
    val srcNodeID = wikiGraph.getNodeID(srcWikiID)

    val dstNodeIDs = dstWikiIDs.map(wikiID => wikiGraph.getNodeID(wikiID))
    val nodeID2index = dstNodeIDs.zipWithIndex.toMap

    val distances = Array.fill(dstNodeIDs.size)(-1)

    var n = 0  // computed distances, used for early termination (n == distances.size)


    // Non-reachable nodes computation
    dstNodeIDs.foreach {
      case dstNodeID =>

        val srcSCC = nodeID2Component(srcNodeID)
        val dstSCC = nodeID2Component(dstNodeID)

        if(srcSCC != dstSCC) {
          n += 1  // nodes that will be not reached by BFS
        }

        if (n == dstNodeIDs.size) return distances
    }


    // Distance computation via BFS + cutpoints
    val bfs = new ParallelBreadthFirstVisit(wikiGraph.graph, 0, false, null)
    bfs.visit(srcNodeID)

    for(distance <- 1 until bfs.cutPoints.size() - 1) {

      val cutIndex = bfs.cutPoints.getInt(distance)
      val cutIndexPlusOne = bfs.cutPoints.getInt(distance + 1)

      for(i <- cutIndex until cutIndexPlusOne) {
        val nodeID = bfs.queue.getInt(i)

        if(nodeID2index.contains(nodeID)) {
          val dstIndex = nodeID2index(nodeID)
          distances(dstIndex) =  distance
        }
      }

    }

    distances
  }

}
