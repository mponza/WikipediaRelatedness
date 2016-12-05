package it.unipi.di.acubelab.wat.dataset.embeddings

import java.io.File
import java.util

import it.cnr.isti.hpc.{LinearAlgebra, Word2VecCompress}
import it.unimi.dsi.fastutil.io.BinIO
import it.unipi.di.acubelab.wat.dataset.Dataset
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.ops.transforms.Transforms

trait EmbeddingsDataset extends Dataset {
  def size: Int
  def translate(words: Seq[String]): Seq[EmbeddingVector] = {
    words.flatMap { w =>
      contains(w) match {
        case true => Some(embedding(w))
        case false => None
      }
    }
  }
  def contains(word: String): Boolean
  def embedding(word: String): EmbeddingVector
  def similarity(w1: String, w2: String): Float

  // New methods

  def similarity(vector1: INDArray, vector2: INDArray): Float =  Transforms.cosineSim(vector1, vector2).toFloat

  def similarity(vector1: INDArray, word: String): Float = {
    throw new IllegalArgumentException("Similarity between vector and word not implemented.")
  }

  //
  def topKSimilarFromWord(word: String, k: Int = 20000): util.Collection[String] = {
    throw new IllegalArgumentException("TopKSimilar function not implemented.")
  }

  def topKSimilarFromWords(words: List[String], k: Int = 20000): util.Collection[String] = {
    throw new IllegalArgumentException("TopKSimilar function with multiple words not implemented.")
  }

  def topKSimilarFromINDArray(vector: INDArray, k: Int = 20000): util.Collection[String] = {
    throw new IllegalArgumentException("Similarity between vector and word not implemented.")
  }

  //
  def contextVector(words: List[String]): INDArray = {
    throw new IllegalArgumentException("contextVector function not implemented")
  }

}

object EmbeddingsDataset {
  def apply(model: WordVectors) = new EmbeddingsDataset {
    override def size: Int = model.vocab().numWords()
    override def similarity(w1: String, w2: String): Float = model.similarity(w1, w2).toFloat
    override def embedding(word: String): EmbeddingVector = {
      if (model.hasWord(word))
        model.getWordVector(word).map(_.toFloat)
      else
        null
    }

    override def contains(word: String): Boolean = model.hasWord(word)

    override def topKSimilarFromWord(word: String, k: Int) = model.wordsNearest(word, k)

    override def topKSimilarFromWords(words: List[String], k: Int) = model.wordsNearest(contextVector(words), k)

    override def topKSimilarFromINDArray(vector: INDArray, k: Int) = model.wordsNearest(vector, k)

    override def similarity(vector: INDArray, word: String): Float = {
      val wordVector = model.getWordVectorMatrix(word)
      super.similarity(vector, wordVector)
    }

    override def contextVector(words: List[String]) = {
      import scala.collection.JavaConversions._
      model.getWordVectorsMean(words)
    }
  }

  def apply(model: Word2VecCompress) = new EmbeddingsDataset {
    def dimEmbedding: Int = model.dimensions()
    def size: Int = model.size()
    def contains(word: String): Boolean = model.word_id(word) != null
    def embedding(word: String): EmbeddingVector = model.get(word)
    def similarity(w1: String, w2: String): Float = {
      val vec1 = embedding(w1)

      if (vec1 == null) {
        return 0f
      }

      val vec2 = embedding(w2)

      if (vec2 == null) {
        return 0f
      }

      val distance = LinearAlgebra.inner(vec1.length, vec1, 0, vec2, 0)
      val srcNorm = math.sqrt(LinearAlgebra.inner(vec1.length, vec1, 0, vec1, 0))
      val dstNorm = math.sqrt(LinearAlgebra.inner(vec2.length, vec2, 0, vec2, 0))

      val cosine = distance / (srcNorm * dstNorm)

      cosine.toFloat
    }
  }

  def apply(file: File): EmbeddingsDataset = {
    if (file.getAbsolutePath.endsWith(".e0.100.tr.bin")) {
      apply(BinIO.loadObject(file).asInstanceOf[Word2VecCompress])
    } else {
      val isBinary = file.getAbsolutePath.endsWith(".bin")
      apply(WordVectorSerializer.loadGoogleModel(file, isBinary))
    }
  }
}
