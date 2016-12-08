package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms.utils

import com.google.common.base.Function
import org.apache.commons.collections15.Transformer

class JungPersonalizedPrior(val wikiID: Int) extends Transformer[Int, java.lang.Double] {

  override def transform(wikiNodeID: Int) = {
    if(wikiNodeID == wikiID) 1.0 else 0.0
  }
}
