package it.unipi.di.acubelab.graphrel.utils


object Configuration {
  val dataset = new {
    val wikiSim = getClass.getResource("/dataset/wikiSim411.csv")
  }

  val wikipedia = new {
    val graph = getClass.getResource("/wikipedia/graph.csv")
  }
}
