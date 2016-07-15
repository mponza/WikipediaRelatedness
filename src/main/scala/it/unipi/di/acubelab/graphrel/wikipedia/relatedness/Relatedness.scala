package it.unipi.di.acubelab.graphrel.wikipedia.relatedness

import it.unipi.di.acubelab.graphrel.dataset.WikiRelTask

trait Relatedness {

  /**
    *
    * @param wikiRelTask
    * @return The relatedness between src and dst of wikiRelTask.
    */
  def computeRelatedness(wikiRelTask: WikiRelTask) : Double
  override def toString() : String
}
