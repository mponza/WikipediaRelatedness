package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.subgraph.topk

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.esa.ESA
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraph
import org.slf4j.LoggerFactory


/**
  * Builds subgraph according to Top-K ESA concepts between srcWikiID and dstWikiID.
  * Threshold is the maximum size (number of nodes) of the subGraph.
  *
  * @param srcWikiID
  * @param dstWikiID
  * @param threshold
  */
class ESASubWikiBVGraph(srcWikiID: Int, dstWikiID: Int, wikiGraph: WikiBVGraph, threshold: Int)
  extends TopKSubWikiBVGraph(srcWikiID, dstWikiID, wikiGraph, threshold) {


  override def getLogger() = LoggerFactory.getLogger(classOf[ESASubWikiBVGraph])


  override def neighborhood(wikiID: Int) : Array[Int] = {
    val neighWikiIDs = ESA.wikipediaConcepts(wikiID, threshold).map(_._1)
    neighWikiIDs.toArray.filter(wikiGraph.contains)
  }

}