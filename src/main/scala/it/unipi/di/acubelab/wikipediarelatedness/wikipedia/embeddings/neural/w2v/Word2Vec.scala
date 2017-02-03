package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.neural.w2v

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.{Embeddings, WikiEmbeddings}
import org.slf4j.{Logger, LoggerFactory}

/**
  * Word2Vec embedding from a model (path).
  *
  * @param model
  */
class Word2Vec(val model: String) extends Embeddings {
  override protected def logger: Logger = LoggerFactory.getLogger(getClass)

  /**
    * Loads entity embeddings from file and returns the corresponding 2-dimensional INDArray.
    *
    * @return
    */
  override protected def loadEmbeddings(): WikiEmbeddings = new Word2VecEmbeddings(model)
}
