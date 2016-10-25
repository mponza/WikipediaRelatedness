package it.unipi.di.acubelab.wikipediarelatedness.options

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessFactory

class PPRSubCosOptions(json: Option[Any]) extends PPRCosOptions(json) {
  val weighting = RelatednessFactory.make(getOptionAny("weighting"))

  override def toString() : String = {
    "%s,weighting:%s".formatLocal(java.util.Locale.US,
      super.toString(), weighting
    )
  }
}