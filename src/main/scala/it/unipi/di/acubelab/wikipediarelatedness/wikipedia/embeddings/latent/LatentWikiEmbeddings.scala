package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.latent

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.WikiEmbeddings
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.ops.transforms.Transforms

trait LatentWikiEmbeddings extends WikiEmbeddings {

  protected def matrix: INDArray // the i-th row is the embedding vector of the i-th WikipediaID.


  /**
    * Adds rowArray to the SVD matrix.
    *
    * @param rowIndex
    * @param rowArray
    * @return
    */
  def putRow(rowIndex: Int, rowArray: Array[Float]) = matrix.putRow( rowIndex, Nd4j.create(rowArray) )


  /**
    * i-th embedding vector.
    *
    * @param wikiID
    * @return
    */
  override def apply(wikiID: Int) : INDArray = matrix.getRow(wikiID)


  def cosine(srcWikiID: Int, dstWikiID: Int, threshold: Int) = {
    val srcVec = this(srcWikiID).slice(threshold)
    val dstVec = this(dstWikiID).slice(threshold)

    Transforms.cosineSim(srcVec, dstVec).toFloat
  }

}
