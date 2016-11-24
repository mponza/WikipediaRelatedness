package it.unipi.di.acubelab.wikipediarelatedness.options

class LLPOptions(json: Option[Any] = None) extends RelatednessOptions(json)  {
  val similarity = getString("similarity", "hamming")
}
