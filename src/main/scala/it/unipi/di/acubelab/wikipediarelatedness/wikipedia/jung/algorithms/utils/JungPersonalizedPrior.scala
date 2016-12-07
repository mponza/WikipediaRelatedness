package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms.utils

import com.google.common.base.Function

class JungPersonalizedPrior(val wikiID: Int) extends Function[Int, java.lang.Double] {

  override def apply(wikiNodeID: Int) = if(wikiNodeID == wikiID) 1.0 else 0.0
}
