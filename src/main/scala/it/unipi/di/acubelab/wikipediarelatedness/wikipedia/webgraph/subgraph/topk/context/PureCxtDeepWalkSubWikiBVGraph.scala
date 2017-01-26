package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.subgraph.topk.context

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.embeddings.TopKEmbeddingsCache
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraph
import org.slf4j.LoggerFactory

class PureCxtDeepWalkSubWikiBVGraph(srcWikiID: Int, dstWikiID: Int, wikiGraph: WikiBVGraph, threshold: Int)
  extends CxtTopKSubWikiBVGraph(srcWikiID, dstWikiID, wikiGraph, threshold) {

  override def getLogger() = LoggerFactory.getLogger(classOf[PureCxtDeepWalkSubWikiBVGraph])

  override def neighborhood(wikiID: Int) : Array[Int] = Array.empty[Int]


  def contextNodes(srcWikiID: Int, dstWikiID: Int): Array[Int] = {
    val embeddings = TopKEmbeddingsCache.deepWalkSG.getTopK(srcWikiID, dstWikiID).map(_._1)
    val noDisEmbeddings = embeddings.filter(wikiGraph.contains)
    logger.debug("Pure Context-Embedding with/without disambiguation %d vs %d".format(embeddings.length, noDisEmbeddings.length))
    noDisEmbeddings.toArray
  }

}
