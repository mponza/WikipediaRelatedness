package it.unipi.di.acubelab.wikipediarelatedness.options

class PPRCosOptions (json: Option[Any]) extends RelatednessOptions(json) {
  val graph = getString("graph")

  val iterations = getInt("iterations", 30)
  val pprDecay = getFloat("pprDecay", 0.8f)

  val weigher = getOptionAny("weighting")

  override def toString() : String = {
    "graph:%s_iters:%d_pprDecay:%1.2f".format(
      graph, iterations, pprDecay
    )
  }
}
