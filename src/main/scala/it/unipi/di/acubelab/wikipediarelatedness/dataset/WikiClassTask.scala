package it.unipi.di.acubelab.wikipediarelatedness.dataset

/**
  * 1 is positive, 0 is negative and -1 is uncertain.
  * @param wikiRelateTask
  */
class WikiClassTask(val wikiRelateTask: WikiRelateTask) {
  val groundClass = getGroundClass()
  var predictedClass = -2


  def getGroundClass(): Int = {
    val humanRelatedness = wikiRelateTask.humanRelatedness

    if(humanRelatedness >= 0.5f && humanRelatedness <= 0.6f) return -1  // uncertain
    if(humanRelatedness < 0.5f) return 0  // negative
    1  // > 0.6f positive
  }

  override def toString() = "%s,GroundTruth:%d,Predicted:%d".format(wikiRelateTask, groundClass, predictedClass)
}