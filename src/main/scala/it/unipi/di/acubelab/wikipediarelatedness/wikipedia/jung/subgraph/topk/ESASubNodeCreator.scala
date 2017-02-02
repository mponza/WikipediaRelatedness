package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph.topk

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.esa.ESA
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory


/**
  * Uses ESA to retrieve the top-k most similar nodes to wikiID.
  *
  * @return
  */
class ESASubNodeCreator(size: Int) extends TopKSubNodeCreator(size) {

  override protected val logger = LoggerFactory.getLogger(getClass)
  protected val graph = WikiBVGraphFactory.make("out")


  override protected def topKNodes(wikiID: Int): Seq[Int] = {
    ESA.wikipediaConcepts(wikiID, 10000).map(_._1).filter(graph.contains)
  }
}
