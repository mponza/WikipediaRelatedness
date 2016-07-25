package it.unipi.di.acubelab.graphrel.benchmark

import java.io.File
import java.nio.file.Paths

import com.github.tototoshi.csv.CSVWriter
import it.unipi.di.acubelab.graphrel.dataset.{RelatednessDataset, WikiRelTask}
import it.unipi.di.acubelab.graphrel.utils.Configuration
import it.unipi.di.acubelab.graphrel.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory


class Benchmark(dataset: RelatednessDataset, relatedness: Relatedness) {
  val logger = LoggerFactory.getLogger(classOf[Benchmark])
  val relDir = Paths.get(Configuration.benchmark, relatedness.toString).toString

  /**
    * Computes relatedness measure on dataset.
    * Write pairs and relatedness to a CSV file under benchmark/.
    */
  def runBenchmark() : Unit = {
    logger.info("running benchmark of %s on dataset %s...".format(relatedness.toString, dataset))

    // List of (wikiRelTask, relatedness computed by using the given relatedenss).
    val relScores = dataset.foldLeft (List.empty[(WikiRelTask, Double)]) {
        case (scores: List[(WikiRelTask, Double)], wikiRelTask: WikiRelTask) =>
          val relScore = relatedness.computeRelatedness(wikiRelTask)
          scores :+ (wikiRelTask, relScore)
      }

    writeRelatednessScores(relScores)
    writeCorrelationScores(relScores)
  }

  /**
    * Writes scores to scoresPath CSV file.
    *
    * @param scores
    */
  def writeRelatednessScores(scores: List[(WikiRelTask, Double)]) : Unit = {
    logger.info("Writing %s Relatedness scores...".format(relatedness.toString))

    new File(relDir).mkdirs
    val path = Paths.get(relDir, relatedness.toString() + ".data.csv").toString

    val csvWriter = CSVWriter.open(new File(path))

    scores.foreach {
      case (wikiRelTask, score) =>

        val csvList = wikiRelTask.toList ++ List(score)
        csvWriter.writeRow(csvList)
    }

    csvWriter.close
  }

  /**
    * Writes both Pearson's and Spearman's correlation to file.
    *
    * @param scores
    */
  def writeCorrelationScores(scores: List[(WikiRelTask, Double)]) : Unit = {
    val pearson = Evaluation.pearsonCorrelation(scores)
    logger.info("%s Pearson: %.2f".format(relatedness.toString, pearson))

    val spearman = Evaluation.spearmanCorrelation(scores)
    logger.info("%s Spearman: %.2f".format(relatedness.toString, spearman))

    new File(relDir).mkdirs
    val path = Paths.get(relDir, relatedness.toString + ".correlation.csv").toString

    val csvWriter = CSVWriter.open(new File(path))

    csvWriter.writeRow(List("Pearson", pearson))
    csvWriter.writeRow(List("Spearman", spearman))

    csvWriter.close
  }
}
