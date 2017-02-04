package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.lucene.vsm

import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.esa.lemma.LemmaLuceneIndex
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessOptions}
import org.slf4j.LoggerFactory


/**
  * Relatedness method based on cosine similarity of the classical vector space model.
  *
  * @param options
  */
class VectorSpaceModelRelatedeness(options: RelatednessOptions)  extends Relatedness {
  val logger = LoggerFactory.getLogger(getClass)
  val lucene = new LemmaLuceneIndex


  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = {

    val srcVSM = lucene.vectorSpaceProjection(srcWikiID).sortBy(_._1)
    val dstVSM = lucene.vectorSpaceProjection(dstWikiID).sortBy(_._1)

    Similarity.cosineSimilarity(srcVSM, dstVSM)
  }

  override def toString = "VectorSpaceModel"
}

