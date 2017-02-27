package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topkcontext.neural

import java.io.File

import it.unipi.di.acubelab.wat.dataset.embeddings.EmbeddingsDataset
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping.WikiTitleID
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topkcontext.TopKContextCached
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory

import scala.collection.mutable.ListBuffer

abstract class NeuralTopKContext extends TopKContextCached {
  protected def modelPath: String  // path to embeddings file
  protected val embeddings: EmbeddingsDataset = EmbeddingsDataset.apply(new File(modelPath))
  protected val wikiBVGraph = WikiBVGraphFactory.make("out")




  /**
    * Scan all embeddings and retireves the topk-similar entities to the average vector between srcWikiID and dstWikiID.
    * Entities which are not present in the Wikipedia graph are dropped.
    *
    * @param k
    * @return
    */
  override protected def nonCachedTopKScoredEntities(srcWikiID: Int, dstWikiID: Int, k: Int): Seq[(Int, Float)] = {
    val scoredEntities = ListBuffer.empty[(Int, Float)]
    val words = List("ent_" + srcWikiID, "ent_" + dstWikiID)


    val wordsIter = embeddings.topKSimilarFromWords(words).iterator()
    val contextVec = embeddings.contextVector(words)

    while (wordsIter.hasNext) {

      val word = wordsIter.next()
      if(word.startsWith("ent_")) {

        try {

          val wID = word.substring(4, word.length).toInt
          if (wikiBVGraph.contains(wID)) {
            val score = embeddings.similarity(contextVec, word)
            scoredEntities += Tuple2(wID, score)

          }

        } catch {
          case e: Exception => logger.error("Error while computing similarity between %s %s and %s"
            .format(WikiTitleID.map(srcWikiID), WikiTitleID.map(dstWikiID), word))
        }

      }
    }

    println("Top-2 for %s %s".format(WikiTitleID.map(srcWikiID), WikiTitleID.map(dstWikiID)))
    scoredEntities.slice(0, 2).foreach {
      case p => println(WikiTitleID.map(p._1), p._2)
    }

    scoredEntities.toList
  }
}
