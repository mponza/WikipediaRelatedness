package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.subgraph.topk

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa.ESA
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraph
import org.slf4j.LoggerFactory


/**
  * Builds subgraph according to Top-K ESA concepts between srcWikiID and dstWikiID.
  * Threshold is the maximum size (number of nodes) of the subGraph.
  *
  * @param srcWikiID
  * @param dstWikiID
  * @param threshold
  */
class ESASubWikiGraph(srcWikiID: Int, dstWikiID: Int, wikiGraph: WikiGraph, threshold: Int)
  extends TopKSubWikiGraph(srcWikiID, dstWikiID, wikiGraph, threshold) {


  override def getLogger() = LoggerFactory.getLogger(classOf[ESASubWikiGraph])


  override def neighborhood(wikiID: Int) : Array[Int] = {
    val neighWikiIDs = ESA.wikipediaConcepts(wikiID, threshold).map(_._1)
    neighWikiIDs.toArray
  }

}