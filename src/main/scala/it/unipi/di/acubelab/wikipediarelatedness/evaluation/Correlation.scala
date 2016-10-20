package it.unipi.di.acubelab.wikipediarelatedness.evaluation

import it.unipi.di.acubelab.wikipediarelatedness.dataset.RelatednessDataset
import org.apache.commons.math.stat.correlation.{PearsonsCorrelation, SpearmansCorrelation}


object Correlation {

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

    val humanScores = dataset.map(task => task.humanRelatedness.toDouble).toArray
    val machineScores = dataset.map(task => task.machineRelatedness.toDouble).toArray

    (humanScores, machineScores)
  }
}