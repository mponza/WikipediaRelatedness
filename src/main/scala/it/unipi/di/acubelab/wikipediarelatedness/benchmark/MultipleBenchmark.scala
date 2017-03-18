package it.unipi.di.acubelab.wikipediarelatedness.benchmark

import java.io.PrintWriter
import java.nio.file.Paths

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateDataset
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory


/**
  * Runs n times Benchmark upon a dataset in order to accurately measure its time performance.
  *
  */
class MultipleBenchmark(dataset: WikiRelateDataset, relatedness: Relatedness, val n: Int = 10)
  extends Benchmark(dataset, relatedness) {

  override protected val logger = LoggerFactory.getLogger(getClass)

  override def run() = {
    val performance = Array.ofDim[Long](n + 1)  // milliseconds for each runRelatedness over all dataset

    for(i <- 0 until n + 1) {
      runRelatedness()
      performance(i) = dataset.map(_.elapsed()).sum
    }

    writeRelatednessScores()
    writeCorrelationScores()
    writeComputationTime(performance.slice(1, 11))
  }


  def writeComputationTime(performance: Array[Long]) = {

    val path = Paths.get(outputDirectory, "time.csv").toString

    val avgTotal = performance.sum / n.toFloat
    val avgPair = performance.map(_ / dataset.size.toFloat).sum / n.toFloat

    logger.info("Average Total Time %1.5f milliseconds." format  avgTotal)
    logger.info("Average Realtedness Time %1.5f milliseconds." format avgPair)

    val writer = new PrintWriter(path)
    writer.write("Total,Average\n%1.5f,%1.5f" format (avgTotal, avgPair))
    writer.close()
  }

}
