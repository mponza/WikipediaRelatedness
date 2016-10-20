package it.unipi.di.acubelab.wikipediarelatedness.options

class GraphSVDOptions (json: Option[Any]) extends RelatednessOptions(json)  {
  val eigen = getString("eigen", "left")
  val length = getInt("length", 100)
}
