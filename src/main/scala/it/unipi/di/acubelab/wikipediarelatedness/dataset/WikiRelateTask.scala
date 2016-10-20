package it.unipi.di.acubelab.wikipediarelatedness.dataset

import java.util.Locale

class WikiRelateTask(val src: WikiEntity, val dst: WikiEntity, val humanRelatedness: Float) {

  var machineRelatedness : Float = Float.NaN


  override def toString() : String = {
    "%s,%s,%1.5f,%1.5f".formatLocal(Locale.US, src, dst, humanRelatedness, machineRelatedness)
  }
}
