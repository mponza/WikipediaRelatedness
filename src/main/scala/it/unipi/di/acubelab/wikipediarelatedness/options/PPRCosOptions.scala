package it.unipi.di.acubelab.wikipediarelatedness.options

class PPRCosOptions (json: Option[Any]) extends RelatednessOptions(json) {
  val graph = getString("graph")

  val iterations = getInt("iterations", 30)
  val decay = getFloat("decay", 0.8f)

  val weigher = getOptionAny("weighting")

}
