package it.unipi.di.acubelab.wikipediarelatedness.benchmark

import java.nio.file.Paths

import it.unipi.di.acubelab.wikipediarelatedness.dataset.{RelatednessDataset, WikiRelTask}
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.ESARelatedness
import org.slf4j.LoggerFactory

/**
  * Experimental class, used to benchmark ESA on the WikiSim dataset.
  * @param dataset
  * @param esa
  */
class WordSimBenchmark(dataset: RelatednessDataset, esa: ESARelatedness)
  extends WikiSimBenchmark(dataset, esa) {
  override val logger = LoggerFactory.getLogger(classOf[WordSimBenchmark])

  override val relDir = Paths.get(Configuration.benchmark, "WORD" + esa.toString).toString

  override def runBenchmark() : Unit = {
    logger.info("Running word similarity benchmark of %s on dataset %s...".format(esa.toString, dataset))

    // List of (wikiRelTask, relatedness computed by using the given relatedenss).
    val relScores = dataset.foldLeft (List.empty[WikiRelTask]) {
      case (relTasks: List[WikiRelTask], wikiRelTask: WikiRelTask) =>

        if(wikiRelTask.srcWord != null && wikiRelTask.srcWord != null) {
          val relScore = esa.relatedness(wikiRelTask.srcWord, wikiRelTask.srcWord)
          relTasks :+ wikiRelTask.make(relScore)
        } else {
          relTasks
        }
    }

    writeRelatednessScores(relScores)

    writeCorrelationScores(relScores)
    writeClassificationScores(relScores)
  }
}
