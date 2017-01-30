package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.embeddings.latent

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.latent.lda.LDA
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessOptions
import org.slf4j.LoggerFactory


/**
  * LDA relatedness by wrapping the latent embedding previously generated with gensim.
  *
  * @param options
  */
class LDARelatedness(options: RelatednessOptions) extends LatentRelatedness(options)  {
  protected val logger = LoggerFactory.getLogger(getClass)
  protected val embeddings = new LDA


  override def toString(): String = {
    if (options.threshold == 0) "LDA"
    else "LDA_threshold:%s".format(options.threshold)
  }
}