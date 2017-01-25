package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.subgraph.topk

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.embeddings.TopKEmbeddingsCache
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiGraph
import org.slf4j.LoggerFactory


class DeepWalkSubWikiGraph(srcWikiID: Int, dstWikiID: Int, wikiGraph: WikiGraph, threshold: Int)
  extends TopKSubWikiGraph(srcWikiID, dstWikiID, wikiGraph, threshold) {


  override def getLogger() = LoggerFactory.getLogger(classOf[DeepWalkSubWikiGraph])


  override def neighborhood(wikiID: Int) : Array[Int] = {
    val embeddings = TopKEmbeddingsCache.deepWalkSG.getTopK(wikiID).map(_._1)

    val noDisEmbeddings = embeddings.filter(wikiGraph.contains)
    logger.debug("Embedding with/without disambiguation %d vs %d".format(embeddings.length, noDisEmbeddings.length))

    noDisEmbeddings.toArray
  }
}