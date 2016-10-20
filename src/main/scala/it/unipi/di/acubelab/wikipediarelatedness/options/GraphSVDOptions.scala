package it.unipi.di.acubelab.wikipediarelatedness.options

class GraphSVDOptions (json: Option[Any]) extends RelatednessOptions(json)  {
  val eigen = getString("eigen", "right")
  val length = getInt("length", 100)  // wtf is this
}
