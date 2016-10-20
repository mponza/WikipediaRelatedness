package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.classification.tuning

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

class WikiTaskFolds(val tasks: List[WikiRelateTask], nBuckets : Int = 10) {
  val logger = LoggerFactory.getLogger(classOf[WikiTaskFolds])
  val folds = generateFolds(tasks, nBuckets)


  def trainEvalTasks() : List[Tuple2[List[WikiRelateTask], List[WikiRelateTask]]] = {
    for ((evalTasks, index) <- folds.zipWithIndex) yield {
      val trainTasks = folds.zipWithIndex.filter(_._2 != index).flatMap(_._1)

      (trainTasks, evalTasks)
    }
  }

  /**
    * Generate tasks into buckets of equal size with the same distribution of positive and negative examples.
    *
    * @return [wikiRelTasks_0, wikiRelTasks_1, ..., wikiRelTasks_nBuckets] where wikiRelTasks is a List[WikiRelTask].
    */
  def generateFolds(tasks: List[WikiRelateTask], nBuckets : Int) : List[List[WikiRelateTask]]= {
    val bucketsBuffer = List.fill(nBuckets)(ListBuffer.empty[WikiRelateTask])

    val positives = tasks.filter(_.humanLabelClass == 1)
    val negatives = tasks.filter(_.humanLabelClass == 0)

    fillBuckets(bucketsBuffer, positives)
    fillBuckets(bucketsBuffer, negatives)

    val buckets = bucketsBuffer.map(_.toList)
    printBucketInformation(buckets)

    buckets
  }


  def fillBuckets(buckets: List[ListBuffer[WikiRelateTask]], samples: List[WikiRelateTask]) = {
    var b = 0

    for(i <- 0 until samples.size) {
      buckets(b) += samples(i)
      b = (b + 1) % buckets.size
    }
  }

  def printBucketInformation(buckets: List[List[WikiRelateTask]]) = {
    buckets.zipWithIndex.foreach {
      case (bucket: List[WikiRelateTask], index: Int) =>

        val pos = bucket.count(_.humanLabelClass == 1)
        val neg = bucket.count(_.humanLabelClass == 1)

        logger.info("%d-bucket: Positivies: %d, Negatives: %d and Size: %d".format(index, pos, neg, bucket.size))
    }
  }
}
