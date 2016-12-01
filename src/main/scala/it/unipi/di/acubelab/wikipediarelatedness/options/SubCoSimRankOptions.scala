package it.unipi.di.acubelab.wikipediarelatedness.options

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessFactory

class SubCoSimRankOptions(json: Option[Any] = None) extends CoSimRankOptions(json) {
  val subGraph = getString("subGraph", "neighborhood")
  val threshold = getInt("threshold", 1000)  // in case of sub-graph generation via ESA

  val weighting = RelatednessFactory.make(getString("weighting"))

  override def toString() : String = {
    "%s,weighting:%s,subGraph:%s".formatLocal(java.util.Locale.US,
      super.toString(), weighting, subGraph
    )
  }
}
