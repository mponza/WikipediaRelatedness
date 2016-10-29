package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask

trait Relatedness {

  /**
    *
    * @param task
    * @return The relatedness between src and dst of the WikiRelTask at hand.
    */
  def computeRelatedness(task: WikiRelateTask) : Float = {
    computeRelatedness(task.src.wikiID, task.dst.wikiID)
  }


  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float

  override def toString() : String
}
