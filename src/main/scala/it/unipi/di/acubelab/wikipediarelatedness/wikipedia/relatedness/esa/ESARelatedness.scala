package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.esa

import it.unipi.di.acubelab.wikipediarelatedness.options.ESAOptions
import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa.ESA
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness



class ESARelatedness(val options: ESAOptions)  extends  Relatedness {


  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    if (srcWikiID == dstWikiID) return 1f

    val srcConcepts = ESA.wikipediaConcepts(srcWikiID, options.threshold)
    val dstConcepts = ESA.wikipediaConcepts(dstWikiID, options.threshold)

    Similarity.cosineSimilarity(srcConcepts, dstConcepts)
  }

  override def toString() : String = { "ESA_%s".format(options) }
}
