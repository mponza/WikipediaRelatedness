package it.unipi.di.acubelab.wikipediarelatedness.classifiers

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiClassTask
// import smile.classification.LogisticRegression

class LogisticRegressor(val lambda: Double) extends Classifier {

  override def trainNpredict(train: List[WikiClassTask], test: List[WikiClassTask]): List[Int] = {
    val (trainFeatures, trainLables) = toFeatureLables(train)
    val (testFeatures, testLables) = toFeatureLables(test)


    List(0)
    //val lg = new LogisticRegression(trainFeatures, trainLables)

    //testFeatures.map(fs => lg.predict(fs)).toList
  }


  def toFeatureLables(tasks: List[WikiClassTask]) : Tuple2[Array[Array[Double]], Array[Int]] = {
    val featureMatrix = tasks.map(task => Array(task.wikiRelateTask.machineRelatedness.toDouble)).toArray
    val lables = tasks.map(_.groundClass.toInt).toArray

    (featureMatrix, lables)
  }
}


object LogisticRegressor {
  //val gammas
}