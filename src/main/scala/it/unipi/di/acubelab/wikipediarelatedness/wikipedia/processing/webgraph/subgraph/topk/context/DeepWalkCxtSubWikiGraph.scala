package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.subgraph.topk.context

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.embeddings.TopKEmbeddingsCache
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraph
import org.slf4j.LoggerFactory


class DeepWalkCxtSubWikiGraph(srcWikiID: Int, dstWikiID: Int, wikiGraph: WikiGraph, threshold: Int)
  extends CxtTopKSubWikiGraph(srcWikiID, dstWikiID, wikiGraph, threshold) {

  override def getLogger() = LoggerFactory.getLogger(classOf[DeepWalkCxtSubWikiGraph])

  override def neighborhood(wikiID: Int) : Array[Int] = {
    // See W2VSubWikiGraph
    val embeddings = TopKEmbeddingsCache.deepWalkSG.getTopK(wikiID).map(_._1)
    val noDisEmbeddings = embeddings.filter(wikiGraph.contains)
    logger.debug("Embedding with/without disambiguation %d vs %d".format(embeddings.length, noDisEmbeddings.length))
    noDisEmbeddings.toArray
  }

  def contextNodes(srcWikiID: Int, dstWikiID: Int): Array[Int] = {
    val embeddings = TopKEmbeddingsCache.corpusSG.getTopK(srcWikiID, dstWikiID).map(_._1)
    val noDisEmbeddings = embeddings.filter(wikiGraph.contains)
    logger.debug("Context-Embedding with/without disambiguation %d vs %d".format(embeddings.length, noDisEmbeddings.length))
    noDisEmbeddings.toArray
  }

}