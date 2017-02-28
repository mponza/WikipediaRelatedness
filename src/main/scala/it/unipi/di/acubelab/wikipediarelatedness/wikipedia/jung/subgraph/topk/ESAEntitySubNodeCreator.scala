package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph.topk

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.ESAEntityTopK
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory


class ESAEntitySubNodeCreator(size: Int) extends TopKSubNodeCreator(size) {
  override protected val logger = LoggerFactory.getLogger(getClass)

  override protected val topK = new ESAEntityTopK
  protected val graph = WikiBVGraphFactory.make("out")


  override def topKNodes(wikiID: Int): Seq[Int] = topK.topKEntities(wikiID, size).filter(graph.contains)
}