package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings

import org.slf4j.Logger


/**
  * Loader of embedding data.
  *
  */
trait Embeddings {

  protected def logger: Logger

  // mapping between a wikiID and its embedding
  protected val embeddings: WikiEmbeddings = loadEmbeddings()


  /**
    * Loads entity embeddings from file and returns the corresponding 2-dimensional INDArray.
    *
    * @return
    */
  protected def loadEmbeddings() : WikiEmbeddings


  def cosine(srcWikiID: Int, dstWikiID: Int) = embeddings.cosine(srcWikiID, dstWikiID)

}
