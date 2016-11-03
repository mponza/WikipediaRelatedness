package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask

trait Relatedness {

  def computeRelatedness(tasks: List[WikiRelateTask]) : Unit = {
    tasks.foreach{
      case task => task.machineRelatedness = computeRelatedness(task)
    }
  }

  /**
    *
    * @param task
    * @return The relatedness between src and dst of the WikiRelTask at hand.
    */
  def computeRelatedness(task: WikiRelateTask) : Float = {
    val greaterZero = Math.max(computeRelatedness(task.src.wikiID, task.dst.wikiID), 0f)
    val lowerOne = Math.min(greaterZero, 1f)

    lowerOne
  }


  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float

  override def toString() : String
}
