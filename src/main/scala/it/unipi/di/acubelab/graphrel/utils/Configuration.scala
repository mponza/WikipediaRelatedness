package it.unipi.di.acubelab.graphrel.utils


object Configuration {

  val CONSTS = new {
    val bvWikiGraphName = "wiki-bv-graph.graph"
  }

  val dataset = new {
    val wikiSim = getClass.getResource("/dataset/wikiSim411.csv")
  }

  val wikipedia = new {
    val directory = getClass.getResource("/wikipedia")
    val wikiLinks = getClass.getResource("/wikipedia/wikiprova.gz") //("/wikipedia/wiki-links-sorted.gz");

    // Generates by BVGraphProcessing.process.
    val bvWikiGraph = getClass.getResource("/wikipedia/" + CONSTS.bvWikiGraphName)
  }
}
