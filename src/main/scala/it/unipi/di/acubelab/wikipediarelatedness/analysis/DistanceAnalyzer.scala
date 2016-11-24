package it.unipi.di.acubelab.wikipediarelatedness.analysis

import java.io.{File, PrintWriter}

import it.unipi.di.acubelab.wikipediarelatedness.dataset.RelatednessDataset
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.distance.DistanceMeter
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.{WikiGraph, WikiGraphFactory}
import org.slf4j.LoggerFactory

class DistanceAnalyzer(val dataset: RelatednessDataset, val wikiGraph: WikiGraph = WikiGraphFactory.outGraph) {
  val logger = LoggerFactory.getLogger(classOf[DistanceAnalyzer])


  def computeDistances(path: String) = {
    val tmpWriter = new PrintWriter(new File(path))


    val distanceMeter = new DistanceMeter(wikiGraph)

    var i = 0
    var errors = 0
    val distances = dataset.map {
      wikiRelTask =>
        val distance = try { distanceMeter.getDistance(wikiRelTask.src.wikiID, wikiRelTask.dst.wikiID) } catch {case e: Exception => errors += 1; -1}
        i += 1
        if (i > 0 && i % 10 == 0) logger.info("*** %d distances computed. ***".format(i))

        tmpWriter.write("%d\n".format(distance))

        distance
    }.toList


    tmpWriter.close()

    logger.warn("Errors: %d".format(errors))
    logger.info("Nodes not too far %d".format(distances.count(_ < 0)))


    val reachedDistances = distances.filter(_ >= 0)
    logger.info("Distance statistics")
    logger.info("Min %d".format(reachedDistances.min))
    logger.info("Max %d".format(reachedDistances.max))

    logger.info("AVG %1.2f".format(reachedDistances.sum / reachedDistances.size.toFloat))
    logger.info("StdDev %1.2f".format(standardDeviation(reachedDistances)))
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