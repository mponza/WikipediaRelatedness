package it.unipi.di.acubelab.wikipediarelatedness.options

class EmbeddingOptions(json: Option[Any]) extends RelatednessOptions(json)  {
  val model = getString("model")
}
