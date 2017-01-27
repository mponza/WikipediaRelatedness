package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.latent

import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.latent.LDA
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessOptions}
import org.slf4j.LoggerFactory
/*

class LDARelatedness(val options: RelatednessOptions) extends Relatedness  {
    val logger = LoggerFactory.getLogger(classOf[LDARelatedness])
    val lda = new LDA()

    override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = {

      val srcVector = lda.embeddings.get(srcWikiID)
      val dstVector = lda.embeddings.get(dstWikiID)

      Similarity.cosineSimilarity(srcVector, dstVector)
    }


    override def toString(): String = {
      "CorpusLDA"
    }
}
*/