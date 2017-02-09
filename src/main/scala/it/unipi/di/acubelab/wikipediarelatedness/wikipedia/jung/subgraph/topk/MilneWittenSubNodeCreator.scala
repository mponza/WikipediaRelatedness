package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph.topk

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.{MilneWittenTopK, TopK}
import org.slf4j.LoggerFactory

class MilneWittenSubNodeCreator(graph: String, size: Int) extends TopKSubNodeCreator(size) {
  override protected val logger = LoggerFactory.getLogger(getClass)

  override protected val topK: TopK = new MilneWittenTopK(graph)

}