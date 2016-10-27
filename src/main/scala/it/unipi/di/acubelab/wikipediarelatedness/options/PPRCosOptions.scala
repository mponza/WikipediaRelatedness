package it.unipi.di.acubelab.wikipediarelatedness.options

class PPRCosOptions (json: Option[Any]) extends RelatednessOptions(json) {
  val iterations = getInt("iterations", 100)
  val pprDecay = getFloat("pprDecay", 0.8f)

  override def toString() : String = {
    "iters:%d,pprDecay:%1.2f".format(
      iterations, pprDecay
    )
  }
}
