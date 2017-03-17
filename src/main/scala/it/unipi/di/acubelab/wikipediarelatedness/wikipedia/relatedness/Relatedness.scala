package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask


trait Relatedness {

  /**
    * Computes the relatedness for each WikiRelateTask by updating the machineRelatedness field.
    *
    * @param tasks
    */
  def computeRelatedness(tasks: Seq[WikiRelateTask]) : Unit = {
    tasks.foreach {
      case task =>
        task.start()
        task.machineRelatedness = computeRelatedness(task)
        task.end()
    }
  }


  /**
    * Computes the relatedness of a WikiRelateTask.
    *
    * @param task
    * @return The relatedness between src and dst of the WikiRelTask at hand.
    */
  def computeRelatedness(task: WikiRelateTask) : Float = {
    val greaterZero = Math.max(computeRelatedness(task.src.wikiID, task.dst.wikiID), 0f)
    val lowerOne = Math.min(greaterZero, 1f)

    lowerOne

    //computeRelatedness(task.src.wikiID, task.dst.wikiID)
  }


  /**
    * Computes the relatedness between two Wikipedia entities uniquely identified by their ID.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float


  /**
    * Relatedness name.
    *
    * @return
    */
  override def toString : String = throw new IllegalArgumentException("Relatedness method without name.")
}
