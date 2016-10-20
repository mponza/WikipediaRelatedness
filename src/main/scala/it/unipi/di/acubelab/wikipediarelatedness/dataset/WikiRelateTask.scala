package it.unipi.di.acubelab.wikipediarelatedness.dataset

import java.util.Locale

class WikiRelateTask(val src: WikiEntity, val dst: WikiEntity, val humanRelatedness: Double) {

  val machineRelatedness = null


  override def toString() : String = {
    "%s,%s,%1.f".formatLocal(Locale.US, src, dst, humanRelatedness, machineRelatedness)
  }
}
