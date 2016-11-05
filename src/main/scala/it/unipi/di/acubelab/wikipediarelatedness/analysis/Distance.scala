package it.unipi.di.acubelab.wikipediarelatedness.analysis

import it.unipi.di.acubelab.wikipediarelatedness.dataset.RelatednessDataset
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.distance.DistanceMeter
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.{WikiGraph, WikiGraphFactory}
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

class Distance(val dataset: RelatednessDataset, val wikiGraph: WikiGraph = WikiGraphFactory.outGraph) {
  val logger = LoggerFactory.getLogger(classOf[Distance])

  def computeDistances() = {
    val distanceMeter = new DistanceMeter(wikiGraph)

    var i = 0
    val distances = dataset.slice(0, 10).map {
      wikiRelTask =>
        val distance = distanceMeter.getDistance(wikiRelTask.src.wikiID, wikiRelTask.dst.wikiID)
        logger.info("Distance between %s and %s is %d.".format(wikiRelTask.src, wikiRelTask.dst))

        i += 1
        if (i > 0 && i % 10 == 0) logger.info("*** %d distances computed. ***".format(i))  // yeah, it's not totally correct...

        distance
    }.toList

    logger.info("Distance statistics")
    logger.info("Min %d".format(distances.min))
    logger.info("Max %d".format(distances.max))

    logger.info("AVG %1.2f".format(distances.sum / distances.size.toFloat))
    logger.info("StdDev %1.2f".format(standardDeviation(distances)))
  }


  protected def standardDeviation(ints: List[Int]) : Float = {
    val avg = ints.sum / ints.size.toFloat

    val powSum = ints.map(n => Math.pow(n - avg, 2.0)).sum.toFloat
    powSum / ints.size.toFloat
  }

}






/*
* def distance(srcWikiID: Int, dstWikiID: Int) : Int = {
 +
 +    if (!distanceCache.contains((srcWikiID, dstWikiID))) {
 +
 +      val distance = bfsDistance(srcWikiID, dstWikiID)
 +      distanceCache.put((srcWikiID, dstWikiID), distance)
 +    }
 +
 +    distanceCache((srcWikiID, dstWikiID))
 +  }
 +
 +
 +  def bfsDistance(srcWikiID: Int, dstWikiID: Int) : Int = {
      if (srcWikiID == dstWikiID) return 0

      val bfs = new ParallelBreadthFirstVisit(bvGraph, 0, false, null)

 -    var prevRound = 0
 -    do {
 -        prevRound = bfs.round
 +    bfs.visit(WikiBVGraph.getNodeID(srcWikiID))
 +
 +    for(d <- 1 until bfs.cutPoints.size - 1) {

 -        // BFS visit and check if dstWikiID has been reached.
 -        bfs.visit(srcWikiID)
 -        if(bfs.queue.contains(WikiBVGraph.getNodeID(dstWikiID))) {
 -          return bfs.round
 -        }
 +      // Get nodes visited at d-th iteration of BFS.
 +      val dIndex = bfs.cutPoints.getInt(d)
 +      val dPlusOneIndex = bfs.cutPoints.getInt(d + 1)
 +      val dNodes = bfs.queue.subList(dIndex, dPlusOneIndex)

 -    } while(prevRound > 0 && prevRound != bfs.round)
 +      if(dNodes.contains(WikiBVGraph.getNodeID(dstWikiID))) return d
 +    }

 -    -1
 +    Int.MaxValue
    }
*
* */