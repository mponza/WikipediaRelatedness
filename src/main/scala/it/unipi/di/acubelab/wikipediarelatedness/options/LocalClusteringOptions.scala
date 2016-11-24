package it.unipi.di.acubelab.wikipediarelatedness.options

class LocalClusteringOptions(json: Option[Any] = None) extends RelatednessOptions(json)  {
  val graph = getString("vector", "inGraph")  // inGraph, outGraph, symGraph, esa

  override def toString() = "graph:%s".format(graph)
}
