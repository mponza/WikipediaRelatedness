package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.dataset.{WikiEntity, WikiRelateTask}

trait Relatedness {

  /**
    *
    * @param task
    * @return The relatedness between src and dst of the WikiRelTask at hand.
    */
  def computeRelatedness(task: WikiRelateTask) : Float = {
    computeRelatedness(task.src.wikiID, task.dst.wikiID)
  }


  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float
  /*def computeRelatredness(srcWikiID: Int, dstWikiID: Int) : Float = {
    val fakeTask =
      new WikiRelateTask(

        new WikiEntity(srcWikiID, "fakeTitle_" + srcWikiID.toString),
        "fakeWord" + srcWikiID.toString,

        new WikiEntity(dstWikiID, "fakeTitle_" + dstWikiID.toString),
        "fakeWord" + dstWikiID.toString,

        Double.NaN, Double.NaN
    )

    computeRelatedness(fakeTask)
  }*/

  override def toString() : String
}
