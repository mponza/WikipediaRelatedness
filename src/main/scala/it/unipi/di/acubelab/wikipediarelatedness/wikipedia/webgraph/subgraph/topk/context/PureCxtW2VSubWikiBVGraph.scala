package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.subgraph.topk.context

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.embeddings.TopKEmbeddingsCache
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraph
import org.slf4j.LoggerFactory

class PureCxtW2VSubWikiBVGraph(srcWikiID: Int, dstWikiID: Int, wikiGraph: WikiBVGraph, threshold: Int)
  extends CxtTopKSubWikiBVGraph(srcWikiID, dstWikiID, wikiGraph, threshold) {

  override def getLogger() = LoggerFactory.getLogger(classOf[PureCxtW2VSubWikiBVGraph])

  override def neighborhood(wikiID: Int) : Array[Int] = Array.empty[Int]

  def contextNodes(srcWikiID: Int, dstWikiID: Int): Array[Int] = {
    val embeddings = TopKEmbeddingsCache.corpusSG.getTopK(srcWikiID, dstWikiID).map(_._1)
    val noDisEmbeddings = embeddings.filter(wikiGraph.contains)
    logger.debug("Pure Context-Embedding with/without disambiguation %d vs %d".format(embeddings.length, noDisEmbeddings.length))
    noDisEmbeddings.toArray
  }

}

