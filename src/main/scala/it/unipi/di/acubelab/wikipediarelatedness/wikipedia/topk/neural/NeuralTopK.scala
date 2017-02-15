package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.neural

import java.io.File

import it.unipi.di.acubelab.wat.dataset.embeddings.EmbeddingsDataset
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping.WikiTitleID
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.TopKCached
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.ListBuffer


abstract class NeuralTopK extends TopKCached {
  protected def modelPath: String  // path to embeddings file
  protected val embeddings: EmbeddingsDataset = EmbeddingsDataset.apply(new File(modelPath))
  protected val wikiBVGraph = WikiBVGraphFactory.make("out")


  /**
    * Scan all embeddings and retireves the topk-similar entities to wikiID.
    * Entities which are not present in the Wikipedia graph are dropped.
    *
    * @param wikiID
    * @param k
    * @return
    */
  override protected def nonCachedTopKScoredEntities(wikiID: Int, k: Int): Seq[(Int, Float)] = {
    val scoredEntities = ListBuffer.empty[(Int, Float)]

    val wordsIter = embeddings.topKSimilarFromWord("ent_" + wikiID).iterator()
    while (wordsIter.hasNext) {

      val word = wordsIter.next()
      if(word.startsWith("ent_")) {

        try {

          val wID = word.substring(4, word.length).toInt
          if (wikiBVGraph.contains(wID)) {

            val score = embeddings.similarity("ent_" + wikiID, "ent_" + wID)
            scoredEntities += Tuple2(wID, score)

          }

        } catch {
          case e: Exception => logger.error("Error while computing similarity between %d and %s"
                                      .format(wikiID, word))
        }

      }
    }

    println("Top-2 for %s".format(WikiTitleID.map(wikiID)))
    scoredEntities.slice(0, 2).foreach {
      case p => println(WikiTitleID.map(p._1), p._2)
    }

    scoredEntities.toList
  }

}