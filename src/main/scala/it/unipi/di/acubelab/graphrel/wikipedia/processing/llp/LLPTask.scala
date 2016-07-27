package it.unipi.di.acubelab.graphrel.wikipedia.processing.llp

import it.unimi.dsi.law.graph.LayeredLabelPropagation

/**
  * LLP parameters.
* @param nLabels
* @param gammaThreshold
* @param maxUpdates
*/

class LLPTask(val nLabels: Int  = LLPTask.DEFAULT_NLABELS,
              val gammaThreshold: Int = LLPTask.DEFAULT_GAMMA_THRESHOLD,
              val maxUpdates: Int = LLPTask.DEFAULT_MAX_UPDATES)
{

  override def toString(): String = {
    val strLabels = if(nLabels != LLPTask.DEFAULT_NLABELS) "-Labels_%d".format(nLabels) else ""
    val strThreshold = if(gammaThreshold != LLPTask.DEFAULT_GAMMA_THRESHOLD) "-Threshold_%d".format(gammaThreshold) else ""
    val strMaxUpdates = if(maxUpdates != LLPTask.DEFAULT_MAX_UPDATES) "-MaxUpdates_%d".format(maxUpdates) else ""

    "llp%s%s%s".format(strLabels, strThreshold, strMaxUpdates)
  }

}

object LLPTask {
  val DEFAULT_NLABELS = 32
  val DEFAULT_GAMMA_THRESHOLD = Integer.MAX_VALUE
  val DEFAULT_MAX_UPDATES = LayeredLabelPropagation.MAX_UPDATES

  def makeFromOption(options: Map[String, Any]): LLPTask = {
    new LLPTask(
      options.getOrElse("nLabels", LLPTask.DEFAULT_NLABELS.toDouble).asInstanceOf[Double].toInt,
      options.getOrElse("gammaThreshold", LLPTask.DEFAULT_GAMMA_THRESHOLD.toDouble).asInstanceOf[Double].toInt,
      options.getOrElse("maxUpdates", LLPTask.DEFAULT_MAX_UPDATES.toDouble).asInstanceOf[Double].toInt
    )
  }
}