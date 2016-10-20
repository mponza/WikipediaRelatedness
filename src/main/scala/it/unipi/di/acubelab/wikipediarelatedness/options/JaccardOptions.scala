package it.unipi.di.acubelab.wikipediarelatedness.options

class JaccardOptions(json: Option[Any]) extends RelatednessOptions(json)  {
  val graph = getString("graph", "inGraph")
}
