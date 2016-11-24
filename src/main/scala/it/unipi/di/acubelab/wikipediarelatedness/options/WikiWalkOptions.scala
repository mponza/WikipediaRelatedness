package it.unipi.di.acubelab.wikipediarelatedness.options

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessFactory

class WikiWalkOptions(json: Option[Any] = None) extends RelatednessOptions(json) {

  val iterations = getInt("iterations", 30)
  val pprDecay = getFloat("pprDecay", 0.8f)

  override def toString() : String = {
    "iters:%d,pprDecay:%1.2f".format(
      iterations, pprDecay
    )
  }
}