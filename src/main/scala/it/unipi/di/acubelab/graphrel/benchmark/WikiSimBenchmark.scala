package it.unipi.di.acubelab.graphrel.benchmark

import java.io.File
import java.nio.file.Paths

import com.github.tototoshi.csv.CSVWriter
import it.unipi.di.acubelab.graphrel.dataset.{RelatednessDataset, WikiSimPair}
import it.unipi.di.acubelab.graphrel.utils.Configuration
import it.unipi.di.acubelab.graphrel.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory

import scala.collection.mutable

class Benchmark(dataset: RelatednessDataset, relatedness: Relatedness) {
  val logger = LoggerFactory.getLogger(classOf[Benchmark])
  val relDir = Paths.get(Configuration.benchmark, relatedness.name()).toString

  /**
    * Computes relatedness measure on dataset.
    * Write pairs and relatedness to a CSV file under benchmark/.
    */
  def generateRelatedness() : Unit = {
    logger.info("Starting benchmarking %s".format(relatedness.name))

    val relScores = dataset.foldLeft (List.empty[(WikiSimPair, Double)]) {
        case (scores: List[(WikiSimPair, Double)], wikiPair: WikiSimPair) =>
          val relScore = relatedness.computeRelatedness(wikiPair.src.wikiID, wikiPair.dst.wikiID)
          scores :+ (wikiPair, relScore)
      }

    writeRelatednessScores(relScores)
    writeCorrelationScores(relScores)
  }

  /**
    * Writes scores to scoresPath CSV file.
    *
    * @param scores
    */
  def writeRelatednessScores(scores: List[(WikiSimPair, Double)]) : Unit = {
    logger.info("Writing %s Relatedness scores...".format(relatedness.name))

    new File(relDir).mkdirs
    val path = Paths.get(relDir, relatedness.name + ".data.csv").toString

    val csvWriter = CSVWriter.open(new File(path))

    scores.foreach {
      case pairScore =>
        val wikiPair = pairScore._1
        val score = pairScore._2

        val csvList = wikiPair.toList ++ List(score)
        println(csvList)
        csvWriter.writeRow(csvList)
    }

    csvWriter.close
  }

  /**
    * Writes both Pearson's and Spearman's correlation to file
    * @param scores
    */
  def writeCorrelationScores(scores: List[(WikiSimPair, Double)]) : Unit = {
    val pearson = Evaluation.pearsonCorrelation(scores)
    logger.info("%s Pearson: %.2f".format(relatedness.name, pearson))

    val spearman = Evaluation.pearsonCorrelation(scores)
    logger.info("%s Spearman: %.2f".format(relatedness.name, spearman))

    new File(relDir).mkdirs
    val path = Paths.get(relDir, relatedness.name + ".correlation.csv").toString

    val csvWriter = CSVWriter.open(new File(path))

    csvWriter.writeRow(List("Pearson", pearson))
    csvWriter.writeRow(List("Spearman", spearman))

    csvWriter.close
  }
}
