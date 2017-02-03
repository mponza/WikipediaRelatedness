package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph.topk

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.ESATopK
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory


/**
  * Uses ESA to retrieve the top-k most similar nodes to wikiID.
  *
  * @return
  */
class ESASubNodeCreator(size: Int) extends TopKSubNodeCreator(size) {

  override protected val logger = LoggerFactory.getLogger(getClass)

  protected val topK = new ESATopK
  protected val graph = WikiBVGraphFactory.make("out")


  override protected def topKNodes(wikiID: Int): Seq[Int] = topK.topKEntities(wikiID, size).filter(graph.contains)
}
