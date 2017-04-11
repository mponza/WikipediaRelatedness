package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast.relatedness

import java.io.File

import it.unipi.di.acubelab.wat.dataset.embeddings.EmbeddingsDataset
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness



class FastEntity2VecRelatedness extends Relatedness {

  val embeddings = loadEmbeddings

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


  protected def loadEmbeddings = {
    val filename = Config.getString("wikipedia.neural.w2v.corpus400")
    EmbeddingsDataset.apply( new File(filename) )
  }

  override def toString = "FastEntit2Vec"
}
