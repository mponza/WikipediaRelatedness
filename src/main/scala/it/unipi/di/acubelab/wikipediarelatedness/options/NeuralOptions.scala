package it.unipi.di.acubelab.wikipediarelatedness.options

class NeuralOptions(json: Option[Any]) extends RelatednessOptions(json)  {
  val model = getString("model", "corpus")
}
