package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.embeddings.latent

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.latent.svd.SVD
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.embeddings.EmbeddingsRelatedness
import org.slf4j.LoggerFactory


/**
  * Embedding via SVD of the graph.
  *
  * @param options
  */
class SVDRelatedness(options: RelatednessOptions) extends EmbeddingsRelatedness(options)  {
  protected val logger = LoggerFactory.getLogger(getClass)
  override protected val embeddings = new SVD


  override def toString: String = {
    if (options.threshold == 0) "SVD"
    else "SVD_threshold:%s".format(options.threshold)
  }
}