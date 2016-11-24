package it.unipi.di.acubelab.wikipediarelatedness.options

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessFactory

class CoSubSimRankOptions(json: Option[Any] = None) extends CoSimRankOptions(json) {
  val subGraph = getString("subGraph", "neighborhood")

  val weighting = RelatednessFactory.make(getOptionAny("weighting"))

  override def toString() : String = {
    "%s,weighting:%s,subGraph:%s".formatLocal(java.util.Locale.US,
      super.toString(), weighting, subGraph
    )
  }
}
