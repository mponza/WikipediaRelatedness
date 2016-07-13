package it.unipi.di.acubelab.graphrel.benchmark

import java.io.File
import java.nio.file.Paths

import com.github.tototoshi.csv.CSVWriter
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unipi.di.acubelab.graphrel.dataset.{RelatednessDataset, WikiSimPair}
import it.unipi.di.acubelab.graphrel.utils.Configuration
import it.unipi.di.acubelab.graphrel.wikipedia.relatedness.Relatedness

class Benchmark(dataset: RelatednessDataset, relatedness: Relatedness) {
  val scoresPath = Paths.get(Configuration.benchmark, relatedness.name()).toString

  /**
    * Generate under the benchmark directory the computed relatedness by using relatedness.
    */
  def generateRelatedness() : Unit = {
    val relScores = new ObjectArrayList[(WikiSimPair, Double)]

    dataset.foreach {
      case (wikiPair: WikiSimPair) =>
        val relScore = relatedness.computeRelatedness(wikiPair.src.wikiID, wikiPair.dst.wikiID)

        relScores.add((wikiPair, relScore))
    }

    writeRelatednessScores(relScores)
  }

  def writeRelatednessScores(scores: ObjectArrayList[(WikiSimPair, Double)]) : Unit = {
    val csvWriter = CSVWriter.open(new File(scoresPath))

    for ((wikiPair: WikiSimPair, score: Double) <- scores) {
      val csvList = wikiPair.toList ++ List(score)
      csvWriter.writeRow(csvList)
    }

    csvWriter.close
  }
}
