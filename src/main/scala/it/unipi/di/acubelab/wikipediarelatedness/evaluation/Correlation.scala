package it.unipi.di.acubelab.wikipediarelatedness.evaluation

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateDataset
import org.apache.commons.math.stat.correlation.{PearsonsCorrelation, SpearmansCorrelation}
import org.slf4j.LoggerFactory


/**
  * Object class for computing correlation scores.
  *
  */
object Correlation {
  protected val logger = LoggerFactory.getLogger("Correlation")


  /**
    * Returns the pearson score between human and machine relatedness of the dataset.
    *
    * @param dataset
    * @return
    */
  def pearson(dataset: WikiRelateDataset) = {
    humanAndMachineScores(dataset) match {

      case (humanScores, machineScores) =>

        new PearsonsCorrelation().correlation(humanScores, machineScores)

      case _ => throw new IllegalArgumentException("Error while computing Pearson correlation.")
    }
  }


  /**
    * Returns the spearman score between human and machine relatedness of the dataset.
    *
    * @param dataset
    * @return
    */
  def spearman(dataset: WikiRelateDataset) = {
    humanAndMachineScores(dataset) match {

      case (humanScores, machineScores) =>

        new SpearmansCorrelation().correlation(humanScores, machineScores)

      case _ =>throw new IllegalArgumentException("Error while computing Spearman correlation.")
    }
  }


  /**
    * Cast the dataset into a pair of arrays, human an machine relatedness scores, respectively.
    *
    * @param dataset
    * @return
    */
  protected def humanAndMachineScores(dataset: WikiRelateDataset) : (Array[Double], Array[Double]) = {

    val datasetNotNaN = dataset.filter(task => !task.machineRelatedness.isNaN).toList
    if (datasetNotNaN.size != dataset.size) logger.warn("************* %d NaN values removed.*************"
                                                        .format(dataset.size - datasetNotNaN.size))

    val humanScores = datasetNotNaN.map(task => task.humanRelatedness.toDouble).toArray
    val machineScores = datasetNotNaN.map(task => task.machineRelatedness.toDouble).toArray

    (humanScores, machineScores)
  }
}