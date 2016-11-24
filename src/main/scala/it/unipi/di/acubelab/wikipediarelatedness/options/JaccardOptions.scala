package it.unipi.di.acubelab.wikipediarelatedness.options

class JaccardOptions(json: Option[Any] = None) extends RelatednessOptions(json)  {
  val graph = getString("graph", "inGraph")
}
