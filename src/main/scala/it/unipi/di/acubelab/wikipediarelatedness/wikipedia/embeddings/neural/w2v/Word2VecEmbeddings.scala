package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.neural.w2v

import java.io.File

import it.unipi.di.acubelab.wat.dataset.embeddings.EmbeddingsDataset
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.WikiEmbeddings
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j


/**
  * Wrapper of EmbeddingDataset.
  *
  * @param modelPath
  */
class Word2VecEmbeddings(modelPath: String) extends WikiEmbeddings {
  protected val w2vDataset = {
    if (modelPath == "") throw new IllegalArgumentException("Model not specificed!.")
    EmbeddingsDataset.apply(new File(modelPath))
  }

  override def apply(wikiID: Int): INDArray = Nd4j.create(w2vDataset.embedding("ent_" + wikiID))

  override def cosine(srcWikiID: Int, dstWikiID: Int) = w2vDataset.similarity("ent_" + srcWikiID, "ent_" + dstWikiID)
}
