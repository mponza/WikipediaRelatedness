package it.unipi.di.acubelab.wikipediarelatedness.evaluation

import it.unipi.di.acubelab.wikipediarelatedness.dataset.RelatednessDataset
import org.apache.commons.math.stat.correlation.{PearsonsCorrelation, SpearmansCorrelation}
import org.slf4j.LoggerFactory

class Correlation {}


object Correlation {

  val logger = LoggerFactory.getLogger(classOf[Correlation])

  def pearson(dataset: RelatednessDataset) = {
    humanAndMachineScores(dataset) match {

      case (humanScores, machineScores) =>

        new PearsonsCorrelation().correlation(humanScores, machineScores)

      case _ => throw new IllegalArgumentException("Human and Machine scores error (wtf).")
    }
  }

  def spearman(dataset: RelatednessDataset) = {
    humanAndMachineScores(dataset) match {

      case (humanScores, machineScores) =>

        new SpearmansCorrelation().correlation(humanScores, machineScores)

      case _ => throw new IllegalArgumentException("Human and Machine scores error (wtf).")
    }
  }

  def humanAndMachineScores(dataset: RelatednessDataset) : (Array[Double], Array[Double]) = {

    val datasetNotNaN = dataset.filter(task => !task.machineRelatedness.isNaN).toList
    if (datasetNotNaN.size != dataset.size) logger.warn("%d NaN values removed.".format(dataset.size - datasetNotNaN.size))

    val humanScores = datasetNotNaN.map(task => task.humanRelatedness.toDouble).toArray
    val machineScores = datasetNotNaN.map(task => task.machineRelatedness.toDouble).toArray

    (humanScores, machineScores)
  }
}