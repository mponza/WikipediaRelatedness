package it.unipi.di.acubelab.wikipediarelatedness.analysis

import java.io.{File, PrintWriter}

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import it.unipi.di.acubelab.wikipediarelatedness.dataset.nyt.NYTDataset
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping.WikiTypeMapping
import org.slf4j.LoggerFactory

import scala.io.Source

class NYTMerger(val dataset: NYTDataset, val pairDistanceFile: String) {
  val logger = LoggerFactory.getLogger(classOf[AllDistanceAnalyzer])


  def mergeNYTWithDistances(outputPath: String) = {
    val distances = loadMappingDistances()

    logger.info("Merging dataset...")

    val writer = new PrintWriter(new File(outputPath))
    writer.write("srcWikiID,srcWikiTitle,srcWikiType,srcNYTFreq,dstWikiID,dstWikiTitle,dstWikiType,dstNYTFreq,coocc,outDist")

    dataset.foreach{
      case nytTask =>

        // x 2 == src and dst
        // (wikiID, wikiTitle, wikiType, NYTFrequency) x 2 , cooccurrence, outDistance
        writer.write(
        "%d,%s,%s,%d,%d,%s,%s,%d,%d,%d".format(

          nytTask.src.wikiID, nytTask.src.wikiTitle, WikiTypeMapping.typePerOrgLoc(nytTask.src.wikiTitle), nytTask.src.frequency,

          nytTask.dst.wikiID, nytTask.dst.wikiTitle, WikiTypeMapping.typePerOrgLoc(nytTask.dst.wikiTitle), nytTask.dst.frequency,

          nytTask.cooccurrence,
          distances.getInt((nytTask.src.wikiID, nytTask.dst.wikiID))
        )
      )
    }


    writer.close()

    logger.info("Dataset merged.")
  }




  def loadMappingDistances() = {
    logger.info("Loading distances...")

    val distances = new Object2IntOpenHashMap[Tuple2[Int, Int]]()

    for(line <- Source.fromFile(pairDistanceFile).getLines()) {
      val fields = line.split(" ")

      distances.put(Tuple2(fields(0).toInt, fields(1).toInt), fields(2).toInt)
    }

    logger.info("Distances loaded.")

    distances
  }
}



/*
*
class AllDistanceAnalyzer(val dataset: NYTDataset, val wikiGraph: WikiGraph = WikiGraphFactory.outGraph)  {
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

            nytTask.distance = distance
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
    logger.info("Nodes not too far %d".format(distances.count(_ < 0))
*
* */