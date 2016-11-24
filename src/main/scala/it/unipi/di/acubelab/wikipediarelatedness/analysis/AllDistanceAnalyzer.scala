package it.unipi.di.acubelab.wikipediarelatedness.analysis

import java.io.{File, PrintWriter}

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unipi.di.acubelab.wikipediarelatedness.dataset.wire.WiReDataset
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.distance.MultipleDistanceMeter
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.{WikiGraph, WikiGraphFactory}
import org.slf4j.LoggerFactory

class AllDistanceAnalyzer(val dataset: WiReDataset, val wikiGraph: WikiGraph = WikiGraphFactory.outGraph)  {
  val logger = LoggerFactory.getLogger(classOf[AllDistanceAnalyzer])

  def computeDistances(path: String) = {
    val writer = new PrintWriter(new File(path))

    val sortedDataset = dataset.toList.sortBy(_.dst.wikiID).groupBy(_.src.wikiID).toList.sortBy(_._2.size)
    val distanceMeter = new MultipleDistanceMeter(wikiGraph)

    logger.info("Computing %d distances...".format(sortedDataset.map(_._2.size).sum))
    var i = 0

    val allDistances = new IntArrayList()

    sortedDataset.foreach {
      case (srcWikiID, nytTasks) =>
        val dstWikiIDs = nytTasks.map(_.dst.wikiID)
        val distances =  distanceMeter.getDistances(srcWikiID, dstWikiIDs)

        nytTasks.zipWithIndex.foreach {
          case (nytTask, index) =>
            val distance = distances(index)

            // Warning: nytTask.outDist = distance
            writer.write("%s".format(nytTask))

            allDistances.add(distance)
        }

        i += nytTasks.size
        logger.info("%d distances computed.".format(i))
    }

    writer.close()

    logger.info("Distances computed.")


    // Statistics

    val distances = allDistances.toIntArray().toList
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