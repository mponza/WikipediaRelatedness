package it.unipi.di.acubelab.wat.dataset.embeddings

import java.io.File
import java.util
import java.util.concurrent.TimeUnit

import it.cnr.isti.hpc.{LinearAlgebra, Word2VecCompress}
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.logging.ProgressLogger
import it.unipi.di.acubelab.wat.dataset.Dataset
import it.unipi.di.acubelab.wikipediarelatedness.utils.{Config, Similarity}
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.ops.transforms.Transforms
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._

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

  def topKSimilarFromWord(word: String, k: Int = 2000): util.Collection[String] = {
    throw new IllegalArgumentException("TopKSimilar function not implFemented.")
  }

  def topKSimilarFromWords(words: List[String], k: Int = 2000): util.Collection[String] = {
    throw new IllegalArgumentException("TopKSimilar function with multiple words not implemented.")
  }

  def topKSimilarFromINDArray(vector: INDArray, k: Int = 2000): util.Collection[String] = {
    throw new IllegalArgumentException("Similarity between vector and word not implemented.")
  }

  //
  def contextVector(words: Seq[String]): INDArray = {
    throw new IllegalArgumentException("contextVector function not implemented")
  }

}

object EmbeddingsDataset {
  protected val logger = LoggerFactory.getLogger("EmbeddingDataset")

  // Used by apply(Word2VecCompress) because vocabulary of words not explicitly available
  lazy val wordEntities = {
    logger.debug("Loading Embedding vocabulary...")
    WordVectorSerializer.loadGoogleModel(  new File(Config.getString("wikipedia.neural.w2v.sg") ), true ).vocab()
      .words().filter(_.startsWith("ent_")).toList
  }

  def apply(model: WordVectors) = new EmbeddingsDataset {

    override def size: Int = model.vocab().numWords()

    override def similarity(w1: String, w2: String): Float = {
      if(!model.hasWord(w1) || !model.hasWord(w2)) return 0f
      model.similarity(w1, w2).toFloat
    }

    override def embedding(word: String): EmbeddingVector = {
      if (model.hasWord(word))
        model.getWordVector(word).map(_.toFloat)
      else
        null
    }

    override def contains(word: String): Boolean = model.hasWord(word)

    //override def topKSimilarFromWord(word: String, k: Int) = model.wordsNearest(word, k)
    override def topKSimilarFromWord(word: String, k: Int) = {
      val vec = model.getWordVectorMatrix(word)

      logger.info("Getting word to INDArray...")
      val wordVecs =  EmbeddingsDataset.wordEntities.map( w => (w, model.getWordVectorMatrix(w)) ).filter(_ != null)

      val pl = new ProgressLogger(logger, 1, TimeUnit.MINUTES)
      pl.start("Computing topK similar from words...")

      val x = wordVecs.par.map {
        case (w, v) =>
          pl.update()
          (w, similarity(vec, v))
      }.toList.sortBy(_._2).reverse.slice(0, k).map(_._1)

      pl.done()

      x

      //EmbeddingsDataset.wordEntities.map {
      //  case entity: String => (entity, similarity(entity, word))
      //}.toList.filter(_ != null).sortBy(_._2).reverse.slice(0, k).map(_._1)
    }

    override def topKSimilarFromWords(words: List[String], k: Int) = model.wordsNearest(contextVector(words), k)

    override def topKSimilarFromINDArray(vector: INDArray, k: Int) = model.wordsNearest(vector, k)

    override def similarity(vector: INDArray, word: String): Float = {
      val wordVector = model.getWordVectorMatrix(word)
      super.similarity(vector, wordVector)
    }

    override def contextVector(words: Seq[String]) = {
      logger.debug("context vector of... %s".format(words.slice(0, 5) mkString " "))
      model.getWordVectorsMean(words.filter(contains(_)))
    }
  }

  def apply(model: Word2VecCompress) = new EmbeddingsDataset {
    protected val logger = LoggerFactory.getLogger(getClass)
    lazy protected val entities = EmbeddingsDataset.wordEntities

    def dimEmbedding: Int = model.dimensions()

    def size: Int = model.size()

    def contains(word: String): Boolean = model.word_id(word) != null

    def embedding(word: String): EmbeddingVector = model.get(word)


    //
    // For now we implement only this
    override def topKSimilarFromWord(word: String, k: Int) = {
      val pl = new ProgressLogger(logger, 1, TimeUnit.MINUTES)
      pl.start("Computing topK similar from compressed embeddings...")

      val x = entities.par.map {
        case entity: String => (entity, similarity(entity, word))
      }.toList.sortBy(_._2).reverse.slice(0, k).map(_._1)
      pl.done()

      x
    }


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

    override def contextVector(words: Seq[String]) = {
      val embeddings = words.filter(contains(_)).map(embedding(_).asInstanceOf[Array[Double]]).toArray
      val ndarray = Nd4j.create(embeddings)
      ndarray.sum(1)
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
