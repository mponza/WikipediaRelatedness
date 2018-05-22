package it.unipi.di.acubelab.wikipediarelatedness.evaluation.task


class WikiRelTask (val src: WikiEntity, val dst: WikiEntity, val humanRelatedness: Float) {
  var machineRelatedness : Float = Float.NaN  // the only mutable field, updated by the chosen relatedness algorithm

  // Computation time fields & methods
  protected var startingTime = 0L
  protected var endingTime = 0L

  def start() = startingTime = System.currentTimeMillis()
  def end() = endingTime = System.currentTimeMillis()
  def elapsed() = endingTime - startingTime
}