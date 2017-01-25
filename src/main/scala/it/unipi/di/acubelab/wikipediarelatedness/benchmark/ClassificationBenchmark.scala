package it.unipi.di.acubelab.wikipediarelatedness.benchmark


import it.unipi.di.acubelab.wikipediarelatedness.classifiers.LinearSVM
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateDataset
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiClassTask
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer


class ClassificationBenchmark(dataset: WikiRelateDataset, relatedness: Relatedness)
  extends RelatednessBenchmark(dataset, relatedness) {
  override val logger = LoggerFactory.getLogger(classOf[ClassificationBenchmark])


  override def runBenchmark() : Unit = {
    super.runBenchmark()
    runClassificationBenchmark()
  }


  def runClassificationBenchmark() = {
    val classTasks = dataset.map(new WikiClassTask(_)).filter(_.groundClass >= 0).toList
    kFoldCrossValidation(classTasks)
  }


  def kFoldCrossValidation(tasks: List[WikiClassTask], k: Int = 4) : List[Float] = {
    val stats = ListBuffer.empty[List[Float]]  // [(prec, rec, f1)] for each k
    val folds = getBalancedFolds(tasks, k)

    logger.info("Running K-Fold Cross-Validation...")
    for(i <- 0 until k) {

      val train = (folds.slice(0, i) ++ folds.slice(i + 1, folds.size)).flatten // all tasks but k-th
      val test = folds(i)

      logger.info("Running %d-th fold...)".format(i))
      logger.info("Training has Pos: %d, Neg: %d".format(train.count(_.groundClass == 1), train.count(_.groundClass == 0)))
      logger.info("Test has has Pos: %d, Neg: %d".format(test.count(_.groundClass == 1), test.count(_.groundClass == 0)))

      // Tune best SVM on training/Validation
      val tuningValidation = getBalancedFolds(train, 2)
      val svm = LinearSVM.gridClassifiers().sortBy(_.evaluate(tuningValidation(0), tuningValidation(1))(2)).reverse.head

      stats += svm.evaluate(train, test)
    }

    // Computes average upon the k-fold cross-validation
    val sumStats = stats.foldLeft(List.fill(3)(0f))((s, prf) => (s, prf).zipped.map(_ + _))
    val avgStats = sumStats.map(_ / stats.size)

    logger.info("Classification performance: P: %1.2f, R: %1.2f, F1: %1.2f"
      .format(avgStats(0), avgStats(1), avgStats(2)))

    avgStats
  }


  def getBalancedFolds(tasks: List[WikiClassTask], k: Int) : List[List[WikiClassTask]] = {
    val positives = tasks.filter(_.groundClass == 1)
    val negatives = tasks.filter(_.groundClass == 0)

    val folds = List.fill(k)(ListBuffer.empty[WikiClassTask])

    positives.zipWithIndex.foreach {
      case (pos, index) => folds(index % k) += pos
    }

    negatives.zipWithIndex.foreach {
      case (neg, index) => folds(index % k) += neg
    }

    folds.map(_.toList)
  }
}