package it.unipi.di.acubelab.wikipediarelatedness.options

class GraphSVDOptions (json: Option[Any] = None) extends RelatednessOptions(json)  {
  val eigen = getString("eigen", "right")
  val length = getInt("length", 100)  // wtf is this

  override def toString() : String = "eigen:%s_length:%d".format(eigen, length)
}
