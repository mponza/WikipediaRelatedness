package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.embeddings

import java.io.File
import java.util

import it.unipi.di.acubelab.wat.dataset.embeddings.EmbeddingsDataset
import it.unipi.di.acubelab.wikipediarelatedness.utils.OldConfiguration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiGraphFactory
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer


/**
  * TopK Embedding with cache.
  *
  * @param modelName
  */
class TopKEmbeddings(modelName: String) {
  val logger = LoggerFactory.getLogger(classOf[TopKEmbeddings])


  lazy val embeddings = EmbeddingsDataset.apply( new File(OldConfiguration.wikipedia(modelName)))
  lazy val cache = new TopKEmbeddingsCache(OldConfiguration.topKEmbeddings(modelName))


  /**
    * List of k entities [(wikiID, weight)] most similar to wikiID.
    *
    * @param wikiID
    * @return
    */
  def getTopK(wikiID: Int): List[Tuple2[Int, Float]] = {
    if (cache.getTopK(wikiID).nonEmpty) return cache.getTopK(wikiID)

    val vector = Nd4j.create(embeddings.embedding("ent_" + wikiID))
    val iter = embeddings.topKSimilarFromWord("ent_" + wikiID).iterator()
    wordIterator2WeightedEntities(vector, iter)
  }


  /**
    * List of k entities [(wikiID, weight)] most similar to average vector of srcWikiID and dstWikiID (context vector).
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  def getTopK(srcWikiID: Int, dstWikiID: Int): List[Tuple2[Int, Float]] = {
    if (cache.getTopK(srcWikiID, dstWikiID).nonEmpty) return cache.getTopK(srcWikiID, dstWikiID)

    val words = List("ent_" + srcWikiID, "ent_" + dstWikiID)
    val vector = embeddings.contextVector(words)
    val iter = embeddings.topKSimilarFromINDArray(vector).iterator()

    wordIterator2WeightedEntities(vector, iter)
  }


  /**
    * From an iterator of words to its list of weighted vector of Wikipedia IDs.
    *
    * @param vector
    * @param it
    * @return
    */
  protected def wordIterator2WeightedEntities(vector: INDArray, it: util.Iterator[String]): List[Tuple2[Int, Float]] = {
    val topKEntities = ListBuffer.empty[Tuple2[Int, Float]]

    while (it.hasNext) {
      val s = it.next()
      try {

        if (s.startsWith("ent_")) {
          val wikiID = s.substring(4, s.length).toInt

          if (isWikiNode(wikiID)) {
            val sim = embeddings.similarity(vector, s)
            topKEntities += Tuple2(wikiID, sim.toFloat)

          } else {
            //logger.warn(wikiID + " is not a Wikipedia Node.")
          }
        }
      } catch {
        case e: Exception => logger.info("Error with %s: %s".format(s.toString, e.toString))
      }
    }

    topKEntities.toList
  }


  protected def isWikiNode(wikiID: Int) = WikiGraphFactory.outGraph.contains(wikiID)

}