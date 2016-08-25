package it.unipi.di.acubelab.wikipediarelatedness.benchmark

import java.io.File
import java.nio.file.Paths

import com.github.tototoshi.csv.CSVWriter
import it.unipi.di.acubelab.wikipediarelatedness.dataset.{RelatednessDataset, WikiRelTask}
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.WikiSimEvaluatorFactory
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.classification.{WikiSimClassPerformance}
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.correlation.WikiSimCorrPerformance
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory


class WikiSimBenchmark(dataset: RelatednessDataset, relatedness: Relatedness) {
  val logger = LoggerFactory.getLogger(classOf[WikiSimBenchmark])
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
    writeClassificationScores(relScores)
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


  def writeCorrelationScores(tasks: List[WikiRelTask]) : Unit = {
    val correlator = WikiSimEvaluatorFactory.make("correlation", tasks)
    val correlation = correlator.wikiSimPerformance().asInstanceOf[WikiSimCorrPerformance]

    logger.info("%s Pearson: %.2f".format(relatedness.toString, correlation.pearson))
    logger.info("%s Spearman: %.2f".format(relatedness.toString, correlation.spearman))

    val path = Paths.get(relDir, relatedness.toString + ".correlation.csv").toString
    correlation.savePerformance(path)
  }


  def writeClassificationScores(tasks: List[WikiRelTask]) : Unit = {
    val classificator = WikiSimEvaluatorFactory.make("classification", tasks)
    val classification = classificator.wikiSimPerformance().asInstanceOf[WikiSimClassPerformance]

    logger.info("%s Classification Scores: %s".format(relatedness.toString, classification.toString))

    val path = Paths.get(relDir, relatedness.toString + ".classification.csv").toString
    classification.savePerformance(path)
  }
}
