package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.esa

import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.esa.ESA
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessOptions}
import org.slf4j.LoggerFactory


/**
  * Explicit Semantic Analysis implementation via CoreNLP and BM25.
  *
  * @param options
  */
class ESARelatedness(val options: RelatednessOptions)  extends  Relatedness {
  val logger = LoggerFactory.getLogger(getClass)

  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    if (srcWikiID == dstWikiID) return 1f

    val srcConcepts = ESA.wikipediaConcepts(srcWikiID, options.threshold).sortBy(_._1)
    val dstConcepts = ESA.wikipediaConcepts(dstWikiID, options.threshold).sortBy(_._1)

    Similarity.cosineSimilarity(srcConcepts, dstConcepts)
  }

  override def toString() : String = { "ESA_threshold:%d".format(options.threshold) }
}
