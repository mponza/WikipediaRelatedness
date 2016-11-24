package it.unipi.di.acubelab.wikipediarelatedness.benchmark

import java.io.{File, PrintWriter}
import java.nio.file.Paths

import it.unipi.di.acubelab.wikipediarelatedness.dataset.{RelatednessDataset, WikiRelateTask}
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.Correlation
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory


class RelatednessBenchmark(val dataset: RelatednessDataset, val relatedness: Relatedness) {
  val logger = LoggerFactory.getLogger(classOf[RelatednessBenchmark])
  val relatednessDirectory = Paths.get(Paths.get(Configuration.benchmark, dataset.toString()).toString,
                                        relatedness.toString).toString

  /**
    * Computes relatedness measure on dataset.
    * Write pairs and relatedness to a CSV file under benchmark/.
    */
  def runBenchmark() = {
    runRelatedness()
    writeRelatednessScores()
    writeCorrelationScores()
  }

  /**
   * Computes machineRelatedness scores for each task in dataset.
   */
  def runRelatedness() : Unit = {
    logger.info("Running Relatedness %s on dataset %s...".format(relatedness.toString, dataset))

    // Computes relatedness for each pair.
    relatedness.computeRelatedness(dataset.toList)
    /*
    dataset.foreach {
      case task: WikiRelateTask =>
        try {
          task.machineRelatedness = relatedness.computeRelatedness(task)
        } catch {
          case e: NoSuchElementException =>
            logger.warn(e.toString)
            task.machineRelatedness = Float.NaN
        }
    }*/
  }

  /**
    * Writes scores to scoresPath CSV file.
    */
  def writeRelatednessScores() : Unit = {
    logger.info("Writing %s Relatedness scores...".format(relatedness.toString))

    new File(relatednessDirectory).mkdirs
    val path = dataPath()

    val writer = new PrintWriter(new File(path))
    // Can be NaN becasue IBMESA removed some pairs not present in their ESA.
    dataset.filter(task => !task.machineRelatedness.isNaN).foreach(task => writer.write(task.toString() + "\n"))
    //dataset.foreach(task => writer.write(task.toString() + "\n"))
    writer.close()
  }


  def writeCorrelationScores() : Unit = {

    val pearson = Correlation.pearson(dataset)
    val spearman = Correlation.spearman(dataset)
    val harmonic = 2 * pearson * spearman / (pearson  + spearman)

    logger.info("%s Pearson: %.2f".format(relatedness.toString, pearson))
    logger.info("%s Spearman: %.2f".format(relatedness.toString, spearman))
    logger.info("%s Harmonic: %.2f".format(relatedness.toString, harmonic))

    val path = correlationPath()

    val writer = new PrintWriter(path)
    writer.write("Pearson:%1.2f\nSpearman: %1.2f".formatLocal(java.util.Locale.US, pearson, spearman))
    writer.close()
  }


  def getPerformance() : List[Float] = {
    val pearson = Correlation.pearson(dataset).toFloat
    val spearman = Correlation.spearman(dataset).toFloat
    val harmonic = 2 * pearson * spearman / (pearson  + spearman)

    List(pearson, spearman, harmonic)
  }



  def dataPath(): String = Paths.get(relatednessDirectory, relatedness.toString() + ".data.csv").toString

  def correlationPath(): String = Paths.get(relatednessDirectory, relatedness.toString + ".correlation.csv").toString
}
