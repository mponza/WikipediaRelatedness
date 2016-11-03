package it.unipi.di.acubelab.wikipediarelatedness.classifiers

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiClassTask
import libsvm._

import scala.collection.mutable.ListBuffer

class LinearSVM(C: Double, posWeight: Double = 1.0, negWeight: Double = 1.0) extends Classifier {
  val parameter = getParameter(C, posWeight, negWeight)


  def getParameter(C: Double, posWeight: Double, negWeight: Double): svm_parameter = {
    val param = new svm_parameter()

    param.C = C
    param.weight = Array(posWeight, negWeight)

    param.svm_type = svm_parameter.C_SVC
    param.kernel_type = svm_parameter.LINEAR

    param.degree = 2
    param.coef0 = 0
    param.nu = 0.5
    param.cache_size = 100
    param.eps = 0.001
    param.p = 0.1
    param.shrinking = 1
    param.probability = 0
    param.nr_weight = 2
    param.weight_label = Array(1, 0)

    param
  }


  def generateProblem(wikiClassTasks: List[WikiClassTask]): svm_problem = {
    val problem = new svm_problem()

    problem.l = wikiClassTasks.size

    // Matrix of examples with their features
    val features = ListBuffer.empty[Array[svm_node]] // examples x feature vector

    for (i <- 0 until problem.l) {

      val node = new svm_node()
      node.index = 1
      node.value = wikiClassTasks(i).wikiRelateTask.machineRelatedness.toDouble

      if (node.value == Float.NaN || node.value < 0 || node.value > 1) {
        throw new IllegalArgumentException("SVM Value wrong value: %1.2f".format(node.value))
      }

      features += Array(node)
    }

    problem.x = features.toArray

    problem.y = wikiClassTasks.map(task => task.groundClass.toDouble).toArray.slice(0, problem.l)

    problem
  }


  override def trainNpredict(train: List[WikiClassTask], test: List[WikiClassTask]): List[Int] = {
    val trainProblem = generateProblem(train)
    val testProblem = generateProblem(test)

    val svmModel = svm.svm_train(trainProblem, parameter)
    testProblem.x.map(node => svm.svm_predict(svmModel, node)).toList.map(_.toInt)
  }
}


object LinearSVM {

  def gridClassifiers(): List[LinearSVM] = {
    for {

      c <- List(1000f, 100f, 10f, 1f, 0.1f, 0.001f, 0.0001f)
      pos <- for (i <- 1 to 10 by 2) yield i
      neg <- for (i <- 1 to 10 by 2) yield i

    } yield new LinearSVM(c.toDouble, pos.toDouble, neg.toDouble)
  }
}