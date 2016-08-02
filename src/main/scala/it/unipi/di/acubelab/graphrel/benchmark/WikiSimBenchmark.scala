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
    logger.info("Running benchmark of %s on dataset %s...".format(relatedness.toString, dataset))

    // List of (wikiRelTask, relatedness computed by using the given relatedenss).
    val relScores = dataset.foldLeft (List.empty[WikiRelTask]) {
        case (relTasks: List[WikiRelTask], wikiRelTask: WikiRelTask) =>
          val relScore = relatedness.computeRelatedness(wikiRelTask)
          relTasks :+ wikiRelTask.make(relScore)
      }

    writeRelatednessScores(relScores)
    writeCorrelationScores(relScores)
  }

  /**
    * Writes scores to scoresPath CSV file.
    *
    * @param tasks
    */
  def writeRelatednessScores(tasks: List[WikiRelTask]) : Unit = {
    logger.info("Writing %s Relatedness scores...".format(relatedness.toString))

    new File(relDir).mkdirs
    val path = Paths.get(relDir, relatedness.toString() + ".data.csv").toString

    val csvWriter = CSVWriter.open(new File(path))

    tasks.foreach {
      case wikiRelTask =>
        csvWriter.writeRow(wikiRelTask.toList )
    }

    csvWriter.close
  }

  /**
    * Writes both Pearson's and Spearman's correlation to file.
    *
    * @param tasks
    */
  def writeCorrelationScores(tasks: List[WikiRelTask]) : Unit = {
    val pearson = Evaluation.pearsonCorrelation(tasks)
    logger.info("%s Pearson: %.2f".format(relatedness.toString, pearson))

    val spearman = Evaluation.spearmanCorrelation(tasks)
    logger.info("%s Spearman: %.2f".format(relatedness.toString, spearman))

    new File(relDir).mkdirs
    val path = Paths.get(relDir, relatedness.toString + ".correlation.csv").toString

    val csvWriter = CSVWriter.open(new File(path))

    csvWriter.writeRow(List("Pearson", pearson))
    csvWriter.writeRow(List("Spearman", spearman))

    csvWriter.close
  }
}
