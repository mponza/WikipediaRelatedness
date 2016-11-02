package it.unipi.di.acubelab.wikipediarelatedness.options

class JaccardTopOptions(json: Option[Any]) extends RelatednessOptions(json)  {
  val graph = getString("graph", "symGraph")
  val threshold = getInt("threshold", 100)
  val ranker = getString("ranker", "clustering")

  override def toString() = "graph:%s_threshold:%d_ranker:%s".format(graph, threshold, ranker)
}
