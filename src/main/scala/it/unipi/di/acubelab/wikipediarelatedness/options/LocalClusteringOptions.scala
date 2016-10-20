package it.unipi.di.acubelab.wikipediarelatedness.options

class LocalClusteringOptions(json: Option[Any]) extends RelatednessOptions(json)  {
  val neighborGraph = getString("neighborGraph", "inGraph")
  val clusterGraph = getString("neighborGraph", "outGraph")
}
