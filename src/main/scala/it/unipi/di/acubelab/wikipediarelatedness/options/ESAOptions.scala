package it.unipi.di.acubelab.wikipediarelatedness.options

class ESAOptions(json: Option[Any]) extends RelatednessOptions(json)  {
  val threshold = getInt("threshold", 625)
}
