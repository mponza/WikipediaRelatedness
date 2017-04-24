package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast.relatedness

import java.io.File

import it.unipi.di.acubelab.wat.dataset.embeddings.EmbeddingsDataset
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config



class FastEntity2VecRelatedness(compressed: Boolean) extends FastEmbeddingRelatedness(compressed) {

  override protected def loadEmbeddings(compressed: Boolean) = {
    EmbeddingsDataset.apply( new File( Config.getString("wikipedia.neural.w2v.corpus400") ) )
  }

  override def toString = "FastE2V-original"

}