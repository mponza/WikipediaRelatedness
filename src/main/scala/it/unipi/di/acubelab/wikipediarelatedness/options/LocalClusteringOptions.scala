package it.unipi.di.acubelab.wikipediarelatedness.options

class LocalClusteringOptions(json: Option[Any]) extends RelatednessOptions(json)  {
  val vectorizer = getString("vector", "inGraph")  // inGraph, outGraph, symGraph, esa
  val similarity = getString("similarity", "jaccard")

  override def toString() = "vector:%s,similarity:%s".format(vectorizer, similarity)
}
