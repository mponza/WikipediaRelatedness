package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.ops.transforms.Transforms


/**
  * Trait of embedding data.
  *
  */
trait WikiEmbeddings {

  def apply(wikiID: Int) : INDArray


  def cosine(srcWikiID: Int, dstWikiID: Int) = {
    val srcVec = this(srcWikiID)
    val dstVec = this(dstWikiID)

    Transforms.cosineSim(srcVec, dstVec).toFloat
  }

}