package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.latent.CorpusLDA
import it.unipi.di.acubelab.wikipediarelatedness.options.LDAOptions
import org.slf4j.LoggerFactory


class LDARelatedness(options: LDAOptions) extends Relatedness  {
    val logger = LoggerFactory.getLogger(classOf[LDARelatedness])
    val corpusLDA = new CorpusLDA()

    override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Double = {

      val srcVector = corpusLDA.lda.get(srcWikiID)
      val dstVector = corpusLDA.lda.get(dstWikiID)

      Similarity.cosineSimilarity(srcVector, dstVector)
    }


    override def toString(): String = {
      "CorpusLDA"
    }
}

