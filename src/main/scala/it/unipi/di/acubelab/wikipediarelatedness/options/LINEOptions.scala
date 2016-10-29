package it.unipi.di.acubelab.wikipediarelatedness.options

class LINEOptions (json: Option[Any]) extends RelatednessOptions(json)  {
  val size = getInt("size", 100)
  val order = getInt("order", 2)
  val negative = getInt("negative", 5)

  override def toString() : String = "size:%d_order:%d_negative:%d".format(size, order, negative)
}