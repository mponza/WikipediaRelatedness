package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.embeddings.latent

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.latent.LatentWikiEmbeddings
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.embeddings.EmbeddingsRelatedness


/**
  * Abstract class for model based on latent decomposition.
  *
  * @param options
  */
abstract class LatentRelatedness(options: RelatednessOptions) extends EmbeddingsRelatedness(options) {


  /**
    * If options.threshold is 0 then the relatedness is computed by deploying the whole embedding size, otheriwise
    * only the FIRST threshold-values are used.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    if (options.threshold == 0) {
      super.computeRelatedness(srcWikiID, dstWikiID)
    } else {
      embeddings.asInstanceOf[LatentWikiEmbeddings].cosine(srcWikiID, dstWikiID, options.threshold)
    }
  }

}
