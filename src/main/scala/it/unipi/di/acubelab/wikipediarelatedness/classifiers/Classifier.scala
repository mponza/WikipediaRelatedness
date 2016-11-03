package it.unipi.di.acubelab.wikipediarelatedness.classifiers

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiClassTask
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.Classification

trait Classifier {

  /**
    *
    * @param train
    * @param test
    * @return [Precision, Recall, F1]
    */
  def evaluate(train: List[WikiClassTask], test: List[WikiClassTask]): List[Float] = {
    val gorundTruth = test.map(_.groundClass.toDouble)
    val predictions = trainNpredict(train, test).map(_.toDouble)

    Classification.precRecF1(predictions, gorundTruth)
  }


  def trainNpredict(train: List[WikiClassTask], test: List[WikiClassTask]): List[Int]
}
