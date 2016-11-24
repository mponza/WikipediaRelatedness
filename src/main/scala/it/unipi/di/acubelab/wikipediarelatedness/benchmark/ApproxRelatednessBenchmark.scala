package it.unipi.di.acubelab.wikipediarelatedness.benchmark


import java.io.PrintWriter
import java.nio.file.Paths

import it.unipi.di.acubelab.wikipediarelatedness.dataset.RelatednessDataset
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.Correlation
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory

class ApproxRelatednessBenchmark(dataset: RelatednessDataset, relatedness: Relatedness)
  extends RelatednessBenchmark(dataset: RelatednessDataset, relatedness: Relatedness) {

  override val logger = LoggerFactory.getLogger(classOf[ApproxRelatednessBenchmark])

  override def runRelatedness() : Unit = {
    super.runRelatedness()
    dataset.foreach(wikiRelTask => wikiRelTask.machineRelatedness = Math.ceil(wikiRelTask.machineRelatedness * 10).toFloat)
  }

  override def dataPath(): String = Paths.get(relatednessDirectory, relatedness.toString() + ".approx.data.csv").toString

  override def correlationPath(): String = Paths.get(relatednessDirectory, relatedness.toString + ".approx.correlation.csv").toString
}
