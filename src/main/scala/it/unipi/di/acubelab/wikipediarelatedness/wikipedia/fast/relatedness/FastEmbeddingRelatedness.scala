package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast.relatedness


import it.unipi.di.acubelab.wat.dataset.embeddings.EmbeddingsDataset
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness


abstract class FastEmbeddingRelatedness(compressed: Boolean) extends Relatedness {

  val embeddings = loadEmbeddings(compressed)

  /**
    * Computes the relatedness between two Wikipedia entities uniquely identified by their ID.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = {
    Math.max( embeddings.similarity("ent_" + srcWikiID, "ent_" + dstWikiID), 0f )
  }


  protected def loadEmbeddings(compressed: Boolean) : EmbeddingsDataset

}