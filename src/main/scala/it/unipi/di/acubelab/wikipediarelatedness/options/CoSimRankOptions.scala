package it.unipi.di.acubelab.wikipediarelatedness.options

class CoSimRankOptions(json: Option[Any]) extends RelatednessOptions(json) {
  val iterations = getInt("iterations", 10)
  val pprDecay = getFloat("pprDecay", 0.8f)
  val csrDecay = getFloat("pprDecay", 0.8f)

  //val weigher = getOptionAny("weighting")

  override def toString() : String = {
    "graph:%s_iters:%d_pprDecay:%1.2f,csrDecay:%1.2f".formatLocal(java.util.Locale.US,
      iterations, pprDecay, csrDecay
    )
  }
}
