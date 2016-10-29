package it.unipi.di.acubelab.wikipediarelatedness.options

class Word2VecOptions(json: Option[Any]) extends RelatednessOptions(json)  {
  val model = getString("model", "corpus")

  override def toString() : String = "model:%s".format(model)
}
