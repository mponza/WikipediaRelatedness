package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast.relatedness

import java.io.File

import it.unipi.di.acubelab.wat.dataset.embeddings.EmbeddingsDataset
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness


class FastDeepWalkRelatedness(compressed: Boolean = false) extends Relatedness {

  val embeddings = loadEmbeddings(compressed)

  /**
    * Computes the relatedness between two Wikipedia entities uniquely identified by their ID.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = {
    embeddings.similarity("ent_" + srcWikiID, "ent_" + dstWikiID)
  }


  protected def loadEmbeddings(compressed: Boolean) = {
    val filename =  if (!compressed) Config.getString("wikipedia.neural.deepwalk.dw10")
                     else Config.getString("wikipedia.neural.deepwalk.ot.dw10")

    EmbeddingsDataset.apply( new File(filename) )
  }

  override def toString = if (!compressed) "FastDeepWalk-original" else "FastDeepWalk-compressed"
}
