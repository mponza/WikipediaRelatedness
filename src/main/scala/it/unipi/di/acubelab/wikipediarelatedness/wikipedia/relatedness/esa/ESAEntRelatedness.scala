package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.esa

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessOptions

class ESAEntRelatedness(options: RelatednessOptions)  extends ESARelatedness(options) {

  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    if(srcWikiID == dstWikiID) 1f

    //relatdness()
    1f
  }

}



/**
  *
  *
class ESARelatedness(options: ESAOptions)  extends  Relatedness {
  val conceptThreshold = options.threshold
  val lucene = getLuceneIndex()


  def getLuceneIndex() = new LemmaLuceneIndex()


  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    if (srcWikiID == dstWikiID) return 1f

    textRelatedness(srcWikiID, dstWikiID)

    //relatedness("ent_%d".format(wikiRelTask.src.wikiID),
    //  "ent_%d".format(wikiRelTask.dst.wikiID))
  }


  def textRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    val srcText = lucene.wikipediaBody(srcWikiID)
    val dstText = lucene.wikipediaBody(dstWikiID)

    println(srcText)

    1f
  }


  override def toString() : String = { "ESA_%s".format(conceptThreshold) }
}

  *
  * */