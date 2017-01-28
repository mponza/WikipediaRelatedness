package it.unipi.di.acubelab.wikipediarelatedness.benchmark

import java.io.{File, PrintWriter}
import java.nio.file.Paths

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateDataset
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.Correlation
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory


class Benchmark(val dataset: WikiRelateDataset, val relatedness: Relatedness) {
  protected val logger = LoggerFactory.getLogger(getClass)
  protected val outputDirectory = benchmarkDirectory()


  /**
    * Returns the directory where save the benchmark information, namely benchmark/dataset/relatedness.
    *
    * @return
    */
  protected def benchmarkDirectory(): String = {
    val benchDir = Config.getString("benchmark.correlation")
    val dataDir = Paths.get(benchDir, dataset.toString()).toString
    val relDir = Paths.get(dataDir, relatedness.toString()).toString

    relDir
  }


  /**
    * Computes relatedness measure on dataset.
    * Write pairs and relatedness to a CSV file under benchmark/.
    *
    */
  def run() = {
    runRelatedness()
    writeRelatednessScores()
    writeCorrelationScores()
  }


  /**
    * Computes machineRelatedness scores for each task in dataset.
    *
    */
  protected def runRelatedness(): Unit = {
    logger.info("Running Relatedness %s on dataset %s...".format(relatedness.toString(), dataset))
    relatedness.computeRelatedness(dataset.toList)
  }


  /**
    * Writes relatedness score into file.
    *
    */
  protected def writeRelatednessScores(): Unit = {
    logger.info("Writing %s Relatedness scores...".format(relatedness.toString()))

    new File(outputDirectory).mkdirs
    val path = Paths.get(outputDirectory, "data.csv").toString

    val writer = new PrintWriter(new File(path))

    // Can be NaN becasue IBMESA removed some pairs not present in their ESA.
    //dataset.filter(task => !task.machineRelatedness.isNaN).foreach(task => writer.write(task.toString() + "\n"))

    dataset.foreach(task => writer.write(task.toString() + "\n"))
    writer.close()
  }


  /**
    * Writes correlation performance into file.
    *
    */
  protected def writeCorrelationScores(): Unit = {
    val pearson = Correlation.pearson(dataset)
    val spearman = Correlation.spearman(dataset)
    val harmonic = 2 * pearson * spearman / (pearson + spearman)

    logger.info("%s Pearson: %.2f".format(relatedness.toString(), pearson))
    logger.info("%s Spearman: %.2f".format(relatedness.toString(), spearman))
    logger.info("%s Harmonic: %.2f".format(relatedness.toString(), harmonic))

    val path = Paths.get(outputDirectory, "correlation.csv").toString

    val writer = new PrintWriter(path)

    writer.write("Pearson: %1.2f\nSpearman: %1.2f\nHarmonic: %1.2f".formatLocal(
      java.util.Locale.US, pearson, spearman, harmonic))

    writer.close()
  }


  /**
    * Returns correlation performance as triple (pearson, spearman, harmonic).
    * @return
    *
    */
  protected def performance(): Seq[Float] = {
    val pearson = Correlation.pearson(dataset).toFloat
    val spearman = Correlation.spearman(dataset).toFloat
    val harmonic = 2 * pearson * spearman / (pearson + spearman)

    List(pearson, spearman, harmonic)
  }

}
