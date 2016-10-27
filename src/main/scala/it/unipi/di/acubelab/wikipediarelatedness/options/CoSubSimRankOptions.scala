package it.unipi.di.acubelab.wikipediarelatedness.options

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessFactory

class CoSubSimRankOptions(json: Option[Any]) extends CoSimRankOptions(json) {

  override val iterations = getInt("iterations", 100)

  val subGraph = getString("subGraph", "neighborhood")

  val weighting = RelatednessFactory.make(getOptionAny("weighting"))

  override def toString() : String = {
    "%s,weighting:%s,subGraph:%s".formatLocal(java.util.Locale.US,
      super.toString(), weighting, subGraph
    )
  }
}
