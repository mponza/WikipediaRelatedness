package it.unipi.di.acubelab.wikipediarelatedness.analysis

import java.io.{File, PrintWriter}

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import it.unipi.di.acubelab.wikipediarelatedness.dataset.wire.WiReDataset
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping.WikiTypeMapping
import org.slf4j.LoggerFactory

import scala.io.Source

class GenerateNYTPreSampling(val dataset: WiReDataset, val outDistanceFile: String, val symDistanceFile: String) {

  val logger = LoggerFactory.getLogger(classOf[GenerateNYTPreSampling])

  val outDistances = loadMappingDistances(outDistanceFile)
  val symDistances = loadMappingDistances(symDistanceFile)


  def loadMappingDistances(path: String) = {
    logger.info("Loading distances from %s...".format(path))

    val distances = new Object2IntOpenHashMap[Tuple2[Int, Int]]()

    for (line <- Source.fromFile(path).getLines()) {
      val fields = line.split(" ")

      distances.put(Tuple2(fields(0).toInt, fields(1).toInt), fields(2).toInt)
    }

    logger.info("Distances loaded.")

    distances
  }



  def enhanceDataset(path: String) = {
    val writer = new PrintWriter(new File(path))

    writer.write("srcWikiID,srcWikiTitle,srcWikiType,srcNYTFreq,dstWikiID,dstWikiTitle,dstWikiType,dstNYTFreq,coocc,label,outDist,symDist\n")

    dataset.foreach {
      case nytTask =>

        if (nytTask.cooccurrence > 10) {


          writer.write("%d,\"%s\",\"%s\",%d,%d,\"%s\",\"%s\",%d,%d,%s,%d,%d\n".format(

                  nytTask.src.wikiID, nytTask.src.wikiTitle, WikiTypeMapping.typePerOrgLoc(nytTask.src.wikiTitle), nytTask.src.frequency,
                  nytTask.dst.wikiID, nytTask.dst.wikiTitle, WikiTypeMapping.typePerOrgLoc(nytTask.dst.wikiTitle), nytTask.dst.frequency,

                  nytTask.cooccurrence, coocc2Label(nytTask.cooccurrence),

                  outDistances.getInt((nytTask.src.wikiID, nytTask.dst.wikiID)),
                  symDistances.getInt((nytTask.src.wikiID, nytTask.dst.wikiID))
              )
          )


        }
    }
    writer.flush()
    writer.close()
  }


  def coocc2Label(cooccurrence: Int) : String = {
    if (cooccurrence < 15) return "tail"
    if (cooccurrence < 25) return "middle"
    "head"
  }


}



/*
*
*
*
*

  def mergeNYTWithDistances(outputPath: String) = {
    val distances = loadMappingDistances()

    logger.info("Merging dataset...")

    val writer = new PrintWriter(new File(outputPath))
    writer.write("srcWikiID,srcWikiTitle,srcWikiType,srcNYTFreq,dstWikiID,dstWikiTitle,dstWikiType,dstNYTFreq,coocc,symDist\n")

    dataset.foreach{
      case nytTask =>

        // x 2 == src and dst
        // (wikiID, wikiTitle, wikiType, NYTFrequency) x 2 , cooccurrence, outDistance
        writer.write(
        "%d,\"%s\",\"%s\",%d,%d,\"%s\",\"%s\",%d,%d,%d\n".format(

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

            nytTask.outDist = distance
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
*
*
* */
