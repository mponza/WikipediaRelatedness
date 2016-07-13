package it.unipi.di.acubelab.graphrel.benchmark

import java.io.File
import java.nio.file.Paths

import com.github.tototoshi.csv.CSVWriter
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unipi.di.acubelab.graphrel.dataset.{RelatednessDataset, WikiSimPair}
import it.unipi.di.acubelab.graphrel.utils.Configuration
import it.unipi.di.acubelab.graphrel.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory

class Benchmark(dataset: RelatednessDataset, relatedness: Relatedness) {
  val logger = LoggerFactory.getLogger(classOf[Benchmark])
  val scoresPath = Paths.get(Configuration.benchmark, relatedness.name()).toString

  /**
    * Computes relatedness measure on dataset.
    * Write pairs and relatedness to a CSV file under benchmark/.
    */
  def generateRelatedness() : Unit = {
    logger.info("Starting benchmarking %s".format(relatedness.name))
    val relScores = new ObjectArrayList[(WikiSimPair, Double)]

    dataset.foreach {
      case (wikiPair: WikiSimPair) =>
        val relScore = relatedness.computeRelatedness(wikiPair.src.wikiID, wikiPair.dst.wikiID)
        relScores.add((wikiPair, relScore))
    }

    writeRelatednessScores(relScores)
  }

  /**
    * Writes scores to scoresPath CSV file.
    * @param scores
    */
  def writeRelatednessScores(scores: ObjectArrayList[(WikiSimPair, Double)]) : Unit = {
    logger.info("Writing %s Relatedness scores...".format(relatedness.name))
    new File(scoresPath).getParentFile.mkdirs
    val csvWriter = CSVWriter.open(new File(scoresPath))

    for (i <- 0 to scores.size - 1) {
      val wikiPair = scores.get(i)._1
      val score = scores.get(i)._2

      val csvList = wikiPair.toList ++ List(score)
      println(csvList)
      csvWriter.writeRow(csvList)
    }

    csvWriter.close
  }
}
