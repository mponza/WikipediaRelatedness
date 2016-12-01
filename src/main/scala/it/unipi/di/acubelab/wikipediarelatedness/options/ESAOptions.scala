package it.unipi.di.acubelab.wikipediarelatedness.options

class ESAOptions(json: Option[Any] = None) extends RelatednessOptions(json)  {
  val threshold = getInt("threshold", 650)

  override def toString() : String = "threshold:%d".format(threshold)
}

