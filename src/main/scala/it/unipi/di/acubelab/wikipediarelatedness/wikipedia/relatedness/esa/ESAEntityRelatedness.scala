package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.esa

import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.esa.ESA
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessOptions

import org.slf4j.LoggerFactory


/**
  * Explicit Semantic Analysis implementation via CoreNLP and BM25 by deploying only ent_wikiID as text.
  *
  * @param options
  */
class ESAEntityRelatedness(options: RelatednessOptions)  extends Relatedness {

  val logger = LoggerFactory.getLogger(getClass)

  /**
    * Computes ESA Relatedness by providing as text ent_wikiID.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    if (srcWikiID == dstWikiID) return 1f

    val srcConcepts = ESA.wikipediaConcepts("ent_%d".format(srcWikiID), options.threshold).sortBy(_._1)
    val dstConcepts = ESA.wikipediaConcepts("ent_%d".format(dstWikiID), options.threshold).sortBy(_._1)

    Similarity.cosineSimilarity(srcConcepts, dstConcepts)
  }

  override def toString() : String = { "ESAEntity_threshold:%d".format(options.threshold) }

}