package it.unipi.di.acubelab.wikipediarelatedness.evaluation.dataset

import it.unipi.di.acubelab.wikipediarelatedness.evaluation.task.WikiRelTask
import org.slf4j.LoggerFactory

trait WikiRelDataset extends Traversable[WikiRelTask] {
  private val logger = LoggerFactory.getLogger(classOf[WikiRelDataset])


  /**
    * Pairs of Wikipedia Entities.
    *
    */
  protected val wikiPairs = loadDataset()


  /**
    * Load dataset as a Seq of WikiRelateTask.
    *
    * @return
    */
  protected def loadDataset() : Seq[WikiRelTask]


  /**
    * Apply a function f on each pair of wikiPairs.
    *
    * @param f
    * @tparam U
    */
  def foreach[U](f: (WikiRelTask) => U) = wikiPairs.foreach(wikiRelTask => f(wikiRelTask))


  def name() : String
}