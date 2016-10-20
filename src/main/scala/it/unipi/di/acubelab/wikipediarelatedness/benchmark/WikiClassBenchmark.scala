package it.unipi.di.acubelab.wikipediarelatedness.benchmark
import it.unipi.di.acubelab.wikipediarelatedness.dataset.{RelatednessDataset, WikiRelateTask}
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness


class WikiClassBenchmark(dataset: RelatednessDataset, relatedness: Relatedness) extends
  WikiSimBenchmark(dataset: RelatednessDataset, relatedness: Relatedness) {

  override def runBenchmark() : Unit = {
    // Removes gray pairs.
    val grayInterval = Configuration.intervals("gray")
    val blackWhiteDataset = dataset.filter(task => !(task.humanRelatedness >= grayInterval._1 && task.humanRelatedness <= grayInterval._2))

    logger.info("Running classification benchmark of %s on dataset %s...".format(relatedness.toString, dataset))
    logger.info("Positive: %d, Negative: %d".format(blackWhiteDataset.count(_.humanLabelClass == 1),
                                                    blackWhiteDataset.count(_.humanLabelClass == 0)))

    // List of (wikiRelTask, relatedness computed by using the given relatedenss).
    val relScores = blackWhiteDataset.foldLeft (List.empty[WikiRelateTask]) {
      case (relTasks: List[WikiRelateTask], wikiRelTask: WikiRelateTask) =>
        val relScore = relatedness.computeRelatedness(wikiRelTask)
        relTasks :+ wikiRelTask.make(relScore)
    }

    writeRelatednessScores(relScores)

    writeCorrelationScores(relScores)
    writeClassificationScores(relScores)
  }
}
