package it.unipi.di.acubelab.wikipediarelatedness.dataset

import org.slf4j.LoggerFactory

/**
  * Base class for each dataset.
  * A subclasses needs to implement loadDataset method.
  *
  */
trait WikiRelateDataset extends Traversable[WikiRelateTask] {
  protected val logger = LoggerFactory.getLogger(getClass)

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
  protected def loadDataset() : Seq[WikiRelateTask]


  /**
    * Apply a function f on each pair of wikiPairs.
    *
    * @param f
    * @tparam U
    */
  def foreach[U](f: (WikiRelateTask) => U) {
    wikiPairs.foreach(wikiRelTask => f(wikiRelTask))
  }


  override def toString() : String  // Name of the dataset
}
