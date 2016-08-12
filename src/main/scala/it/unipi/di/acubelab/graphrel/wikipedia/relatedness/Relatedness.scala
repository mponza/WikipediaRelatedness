package it.unipi.di.acubelab.graphrel.wikipedia.relatedness

import it.unipi.di.acubelab.graphrel.dataset.{WikiEntity, WikiRelTask}

trait Relatedness {

  /**
    *
    * @param wikiRelTask
    * @return The relatedness between src and dst of wikiRelTask.
    */
  def computeRelatedness(wikiRelTask: WikiRelTask) : Double

  def computeRelatredness(srcWikiID: Int, dstWikiID: Int) : Double = {
    val fakeTask =
      new WikiRelTask(

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
