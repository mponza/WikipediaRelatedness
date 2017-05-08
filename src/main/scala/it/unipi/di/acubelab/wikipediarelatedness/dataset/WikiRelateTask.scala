package it.unipi.di.acubelab.wikipediarelatedness.dataset

import java.util.Locale


/**
  * Wikipedia Relatedness Task. The field machineRelatedness is filled by a relatedness method.
  *
  * @param src
  * @param dst
  * @param humanRelatedness
  */
class WikiRelateTask(val src: WikiEntity, val dst: WikiEntity, val humanRelatedness: Float) {
  var machineRelatedness : Float = Float.NaN  // the only mutable field, updated by the chosen relatedness algorithm

  // Computation time fields & methods
  protected var startingTime = 0L
  protected var endingTime = 0L

  def start() = startingTime = System.currentTimeMillis()
  def end() = endingTime = System.currentTimeMillis()
  def elapsed() = endingTime - startingTime


  override def toString() : String = {
    "\"%s\",%d,\"%s\",%d,%1.5f,%1.5f".formatLocal(Locale.US, src.wikiTitle, src.wikiID, dst.wikiTitle, dst.wikiID, humanRelatedness, machineRelatedness)
  }
}

