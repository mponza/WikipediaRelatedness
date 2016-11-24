package it.unipi.di.acubelab.wikipediarelatedness.options

class CoSimRankOptions(json: Option[Any] = None) extends RelatednessOptions(json) {
  val iterations = getInt("iterations", 30)
  val pprDecay = getFloat("pprDecay", 0.8f)
  val csrDecay = getFloat("csrDecay", 0.8f)

  //val weigher = getOptionAny("weighting")

  override def toString() : String = {
    "iters:%d_pprDecay:%1.2f,csrDecay:%1.2f".formatLocal(java.util.Locale.US,
      iterations, pprDecay, csrDecay
    )
  }
}
