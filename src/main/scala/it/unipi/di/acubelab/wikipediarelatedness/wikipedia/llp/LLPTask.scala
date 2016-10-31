package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.llp

import it.unimi.dsi.law.graph.LayeredLabelPropagation

/**
  * Task configuration of LLP.
  */
class LLPTask(val gammas: List[Float] = LLPTask.gammas,
               val maxUpdates: Int = LayeredLabelPropagation.MAX_UPDATES) {

  override def toString(): String = {
    "gammas:%s"
  }

}


object LLPTask {
  val gammas = List(10f, 5f, 2f, 1f, 0.5f, 0.25f, 0.125f, 0.0625f, 0.03125f, 0.015625f, 0.0f)
}
