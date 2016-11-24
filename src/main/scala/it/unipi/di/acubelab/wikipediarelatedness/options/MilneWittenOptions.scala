package it.unipi.di.acubelab.wikipediarelatedness.options

class MilneWittenOptions(json: Option[Any] = Some()) extends RelatednessOptions(json)  {
  val graph = getString("graph", "inGraph")
}
