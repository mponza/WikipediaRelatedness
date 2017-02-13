package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.neural.w2v

import java.io.File

import it.unipi.di.acubelab.wat.dataset.embeddings.EmbeddingsDataset
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.WikiEmbeddings
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.slf4j.LoggerFactory


/**
  * Wrapper of EmbeddingDataset.
  *
  * @param modelPath
  */
class Word2VecEmbeddings(modelPath: String) extends WikiEmbeddings {
  protected val logger = LoggerFactory.getLogger(getClass)

  protected val w2vDataset = {
    if (modelPath == "") throw new IllegalArgumentException("Model not specificed!.")
    EmbeddingsDataset.apply(new File(modelPath))
  }

  override def apply(wikiID: Int): INDArray = {
    if (!w2vDataset.contains("ent_" + wikiID)) {

      //logger.warn("%d not present in %s w2v model. Returning null vector."format(wikiID, WikiTitleID.map(wikiID)))
      null

    } else {

      Nd4j.create(w2vDataset.embedding("ent_" + wikiID))
    }
  }

}
