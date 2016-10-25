package it.unipi.di.acubelab.wikipediarelatedness.options

class IBMESAOptions(json: Option[Any]) extends RelatednessOptions(json) {
  val threshold = getInt("threshold", 650)

  override def toString() = "%d".format(threshold)
}
