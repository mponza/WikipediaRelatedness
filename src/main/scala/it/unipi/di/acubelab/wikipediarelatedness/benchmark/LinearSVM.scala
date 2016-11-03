package it.unipi.di.acubelab.wikipediarelatedness.benchmark

import it.unipi.di.acubelab.wikipediarelatedness.dataset.{WikiClassTask, WikiRelateTask}
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.Classification
import libsvm._

class LinearSVM(C: Double) {
  val parameter = getParameter(C)


  def getParameter(C: Double) : svm_parameter = {
    val param = new svm_parameter()

    param.svm_type = svm_parameter.C_SVC
    param.kernel_type = svm_parameter.LINEAR

    param.C = C

    param
  }


  def generateProblem(wikiClassTasks: List[WikiClassTask]) : svm_problem = {
    val problem = new svm_problem()

    problem.l = wikiClassTasks.size

    problem.x = Array.ofDim[svm_node](problem.l, 1)
    for(i <- 0 until problem.l) {

      val node = new svm_node()
      node.index = i
      node.value = wikiClassTasks(i).wikiRelateTask.machineRelatedness

      problem.x(i)(0) = node
    }


    problem.y = wikiClassTasks.map(task => task.groundClass.toDouble).toArray

    problem
  }


  def evaluate(train: List[WikiClassTask], test: List[WikiClassTask]) : List[Float] = {
    val trainProblem = generateProblem(train)
    val testProblem = generateProblem(test)

    val svmModel = svm.svm_train(trainProblem, parameter)
    val predictions = testProblem.x.map(node => svm.svm_predict(svmModel, node))

    Classification.precRecF1(predictions, testProblem.y)
  }


}


// fare wegiht label e weight