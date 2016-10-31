package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.oldllp

import it.unimi.dsi.law.graph.LayeredLabelPropagation

/**
  * LLP parameters.
  * @param nGammas
  * @param gammaThreshold
  * @param maxUpdates
  */
class LLPTask(val nGammas: Int  = LLPTask.DEFAULT_NGAMMAS,
              val gammaThreshold: Int = LLPTask.DEFAULT_GAMMA_THRESHOLD,
              val maxUpdates: Int = LLPTask.DEFAULT_MAX_UPDATES)
{

  override def toString(): String = {
    val strLabels = "-Labels_%d".format(nGammas)
    val strThreshold = if(gammaThreshold != LLPTask.DEFAULT_GAMMA_THRESHOLD) "-Threshold_%d".format(gammaThreshold) else ""
    val strMaxUpdates = if(maxUpdates != LLPTask.DEFAULT_MAX_UPDATES) "-MaxUpdates_%d".format(maxUpdates) else ""

    "llp%s%s%s".format(strLabels, strThreshold, strMaxUpdates)
  }

  def toOptions() : Map[String, Any] = {
    Map(
      "nLabels" -> nGammas.toDouble,
      "gammaThreshold" -> gammaThreshold.toDouble,
      "maxUpdates" -> maxUpdates.toDouble
    )
  }

}

object LLPTask {
  val DEFAULT_NGAMMAS = 32
  val DEFAULT_GAMMA_THRESHOLD = Integer.MAX_VALUE
  val DEFAULT_MAX_UPDATES = LayeredLabelPropagation.MAX_UPDATES

  def makeFromOption(options: Map[String, Any]): LLPTask = {
    val x = new LLPTask(
      options.getOrElse("nLabels", LLPTask.DEFAULT_NGAMMAS.toDouble).asInstanceOf[Double].toInt,
      options.getOrElse("gammaThreshold", LLPTask.DEFAULT_GAMMA_THRESHOLD.toDouble).asInstanceOf[Double].toInt,
      options.getOrElse("maxUpdates", LLPTask.DEFAULT_MAX_UPDATES.toDouble).asInstanceOf[Double].toInt
    )
    println(x)
    x
  }
}