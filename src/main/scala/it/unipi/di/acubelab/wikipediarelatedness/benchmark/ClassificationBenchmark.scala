package it.unipi.di.acubelab.wikipediarelatedness.benchmark


import it.unipi.di.acubelab.wikipediarelatedness.dataset.RelatednessDataset
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiClassTask
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer


class ClassificationBenchmark(dataset: RelatednessDataset, relatedness: Relatedness)
  extends RelatednessBenchmark(dataset, relatedness) {
  override val logger = LoggerFactory.getLogger(classOf[ClassificationBenchmark])


  override def runBenchmark() : Unit = {
    super.runBenchmark()
    runClassificationBenchmark()
  }


  def runClassificationBenchmark() = {
    val classTasks = dataset.map(new WikiClassTask(_)).filter(_.groundClass >= 0)

  }


  def kFoldCrossValidation(tasks: List[WikiClassTask]) : List[Float] = {
    val stats = ListBuffer.empty[List[Float]]  // [(prec, rec, f1)] for each k

    logger.info("Running K-Fold Cross-Validation...")
    for(k <- 0 until tasks.size) {
      val train = tasks.slice(0, k) ++ tasks.slice(k + 1, tasks.size)  // all tasks but k-th
      val test = List(tasks(k))

      val (training, validation)  = getBalancedSplit(train)

      // best SVM
      val svm = LinearSVM.gridClassifiers().sortBy(_.evaluate(training, validation)(2)).reverse.head

      stats += svm.evaluate(train, test)
    }

    // Computes average upon the k-fold cross-validation
    val sumStats = stats.foldLeft(List.fill(3)(0f))((s, prf) => (s, prf).zipped.map(_ + _))
    val avgStats = sumStats.map(_ / stats.size)

    logger.info("Classification performance: P: %1.2f, R: %1.2f, F1: %1.2f"
      .format(avgStats(0), avgStats(1), avgStats(2)))

    avgStats
  }


  def getBalancedSplit(tasks: List[WikiClassTask]) : Tuple2[List[WikiClassTask], List[WikiClassTask]] = {
    val positives = tasks.filter(_.groundClass == 1)
    val negatives = tasks.filter(_.groundClass == 0)

    val training = positives.slice(0, positives.size / 2) ++ negatives.slice(0, negatives.size / 2)
    val validation = positives.slice(positives.size / 2, positives.size) ++ negatives.slice(negatives.size / 2, negatives.size)

    (training, validation)
  }
}