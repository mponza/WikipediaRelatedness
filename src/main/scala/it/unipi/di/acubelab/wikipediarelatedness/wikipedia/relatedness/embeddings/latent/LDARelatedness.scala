package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.embeddings.latent

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.latent.lda.LDA
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.embeddings.EmbeddingsRelatedness
import org.slf4j.LoggerFactory


/**
  * LDA relatedness by wrapping the latent embedding previously generated with gensim.
  *
  * @param options
  */
class LDARelatedness(options: RelatednessOptions) extends EmbeddingsRelatedness(options)  {
  protected val logger = LoggerFactory.getLogger(getClass)
  override protected val embeddings = new LDA(options.threshold)


  override def toString(): String = {
    if (options.threshold == 0) "LDA"
    else "LDA_threshold:%s".format(options.threshold)
  }
}