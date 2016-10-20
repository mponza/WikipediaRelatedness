package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.latent.CorpusLDA
import org.slf4j.LoggerFactory


class LDARelatedness(options: Map[String, Any]) extends Relatedness  {
    val logger = LoggerFactory.getLogger(classOf[LDARelatedness])
    val corpusLDA = new CorpusLDA()

    override def computeRelatedness(wikiRelTask: WikiRelateTask): Double = {
      val (srcWikiID, dstWikiID) = wikiRelTask.wikiIDs

      val srcVector = corpusLDA.lda.get(srcWikiID)
      val dstVector = corpusLDA.lda.get(dstWikiID)

      Similarity.cosineSimilarity(srcVector, dstVector)
    }


    override def toString(): String = {
      "CorpusLDA"
    }
}

