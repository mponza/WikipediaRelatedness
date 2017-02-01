package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms.ppr

import org.apache.commons.collections15.Transformer


/**
  * Prior vector with 1.0 for wikiID, 0.0 otherwise.
  *
  * @param wikiID
  */
class StandardBasisPrior(val wikiID: Int) extends Transformer[Int, java.lang.Double] {

  override def transform(wikiNodeID: Int) = {

    if(wikiNodeID == wikiID) 1.0 else 0.0
  }
}
