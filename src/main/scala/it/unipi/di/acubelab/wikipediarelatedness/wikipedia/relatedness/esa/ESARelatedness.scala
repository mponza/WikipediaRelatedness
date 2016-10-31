package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.esa

import it.unipi.di.acubelab.wikipediarelatedness.options.ESAOptions
import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa.lemma.LemmaLuceneIndex
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness



class ESARelatedness(options: ESAOptions)  extends  Relatedness {
  val conceptThreshold = options.threshold
  val lucene = getLuceneIndex()


  def getLuceneIndex() = new LemmaLuceneIndex()


  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    if (srcWikiID == dstWikiID) return 1f

    val srcConcepts = lucene.wikipediaConcepts(srcWikiID)
    val dstConcepts = lucene.wikipediaConcepts(dstWikiID)

    Similarity.cosineSimilarity(srcConcepts, dstConcepts)
  }

  override def toString() : String = { "ESA_%s".format(options) }
}
