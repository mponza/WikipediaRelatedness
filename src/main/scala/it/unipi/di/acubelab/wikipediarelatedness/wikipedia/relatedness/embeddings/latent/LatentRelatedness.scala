package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.embeddings.latent

import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.embeddings.EmbeddingsRelatedness

abstract class LatentRelatedness(options: RelatednessOptions) extends EmbeddingsRelatedness(options) {


  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {

    val srcVector = thresholdedEmbedding(srcWikiID)
    val dstVector = thresholdedEmbedding(dstWikiID)

    Similarity.cosineSimilarity(srcVector, dstVector)
  }


  /**
    * Returns the embedding vector of WikiID of size options.threshold.
    *
    * @param wikiID
    * @return
    */
  protected def thresholdedEmbedding(wikiID: Int) = {
    val vector = embeddings(wikiID)
    if (options.threshold == 0) {
      vector
    } else {
      vector.slice(0, options.threshold)
    }

  }

}
