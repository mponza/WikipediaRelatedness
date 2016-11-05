package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.distance

import it.unimi.dsi.webgraph.algo.ParallelBreadthFirstVisit
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraph

class DistanceMeter(val wikiGraph: WikiGraph) {

  val cache = new DistanceCache

  def getDistance(srcWikiID: Int, dstWikiID: Int) : Int = {
    if (srcWikiID == dstWikiID) return 0

    val srcNodeID = wikiGraph.getNodeID(srcNodeID)
    val dstNodeID = wikiGraph.getNodeID(srcNodeID)

    // Src has been already visited
    val cachedDistance = cache.getDistance(srcNodeID, dstNodeID)
    if (cachedDistance != null) return cachedDistance

    // New distance computation
    val bfs = computeBFS(srcNodeID)

    cache.updateCacheNgetDistance(srcNodeID, dstNodeID, bfs)
  }


  protected def computeBFS(srcNodeID: Int) : ParallelBreadthFirstVisit = {
    val maxDistance = 4

    val bfs = new ParallelBreadthFirstVisit(wikiGraph.graph, 0, false, null)
    for (i <- 0 until maxDistance) bfs.visit(srcNodeID)

    bfs
  }
}