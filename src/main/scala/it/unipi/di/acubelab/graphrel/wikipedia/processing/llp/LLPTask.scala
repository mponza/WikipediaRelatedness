package it.unipi.di.acubelab.graphrel.wikipedia.processing.llp

import it.unimi.dsi.law.graph.LayeredLabelPropagation

/**
  * LLP parameters.
* @param nLabels  aka number of gammas/clusters
* @param gammaThreshold Ha davvero senso?
* @param maxUpdates
*/

class LLPTask(val nLabels: Int  = LLPTask.DEFAULT_NLABELS,
              val gammaThreshold: Int = LLPTask.DEFAULT_GAMMA_THRESHOLD,
              val maxUpdates: Int = LLPTask.DEFAULT_MAX_UPDATES)
{

  override def toString(): String = {
    "llp-Labels_%s-Threshold_%s-MaxUpdates_%s".format(nLabels, gammaThreshold, maxUpdates)
  }

}

object LLPTask {
  val DEFAULT_NLABELS = 10
  val DEFAULT_GAMMA_THRESHOLD = Integer.MAX_VALUE
  val DEFAULT_MAX_UPDATES = LayeredLabelPropagation.MAX_UPDATES

  def makeFromOption(options: Map[String, Any]): LLPTask = {
    new LLPTask(
      options.getOrElse("nLabels", LLPTask.DEFAULT_NLABELS).asInstanceOf[Double].toInt,
      options.getOrElse("gammaThreshold", LLPTask.DEFAULT_GAMMA_THRESHOLD).asInstanceOf[Double].toInt,
      options.getOrElse("maxUpdates", LLPTask.DEFAULT_MAX_UPDATES).asInstanceOf[Double].toInt
    )
  }
}