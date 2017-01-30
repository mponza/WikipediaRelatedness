package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.embeddings.latent

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.latent.SVD
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessOptions
import org.slf4j.LoggerFactory


/**
  * Embedding via SVD of the graph.
  *
  * @param options
  */
class SVDRelatedness(options: RelatednessOptions) extends LatentRelatedness(options)  {
  protected val logger = LoggerFactory.getLogger(getClass)
  protected val embeddings = new SVD


  override def toString(): String = {
    "SVD_threshold:%s".format(options.threshold)
  }
}