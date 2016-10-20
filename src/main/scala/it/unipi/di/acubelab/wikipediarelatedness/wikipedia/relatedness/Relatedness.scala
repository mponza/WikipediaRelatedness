package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.dataset.{WikiEntity, WikiRelateTask}

trait Relatedness {

  /**
    *
    * @param wikiRelTask
    * @return The relatedness between src and dst of wikiRelTask.
    */
  def computeRelatedness(wikiRelTask: WikiRelateTask) : Double

  def computeRelatredness(srcWikiID: Int, dstWikiID: Int) : Double = {
    val fakeTask =
      new WikiRelateTask(

        new WikiEntity(srcWikiID, "fakeTitle_" + srcWikiID.toString),
        "fakeWord" + srcWikiID.toString,

        new WikiEntity(dstWikiID, "fakeTitle_" + dstWikiID.toString),
        "fakeWord" + dstWikiID.toString,

        Double.NaN, Double.NaN
    )

    computeRelatedness(fakeTask)
  }

  override def toString() : String
}
