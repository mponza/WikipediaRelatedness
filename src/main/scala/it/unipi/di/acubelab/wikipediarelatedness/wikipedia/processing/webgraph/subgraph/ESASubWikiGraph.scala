package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.subgraph

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa.ESA

class ESASubWikiGraph(srcWikiID: Int, dstWikiID: Int) extends SubWikiGraph(srcWikiID, dstWikiID) {

  override def neighborhood(srcWikiID: Int) : Array[Int] = {
    val neighWikiIDs = ESA.wikipediaConcepts(srcWikiID).map(_._1)

    neighWikiIDs.map(wikiID => outGraph.getNodeID(wikiID)).toArray
  }

}