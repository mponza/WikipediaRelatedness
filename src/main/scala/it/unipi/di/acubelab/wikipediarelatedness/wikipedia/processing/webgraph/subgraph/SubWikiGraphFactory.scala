package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.subgraph

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraphFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.subgraph.topk.ESASubWikiGraph


object SubWikiGraphFactory {

  def make(subGraph: String, srcWikiID: Int, dstWikiID: Int,
           wikiGraph: WikiGraph = WikiGraphFactory.outGraph, threshold : Int = 1000)

    = subGraph match {

    case "neigh" | "neighborhood" => new NeighSubWikiGraph(srcWikiID, dstWikiID, wikiGraph)
    case "esa" => new ESASubWikiGraph(srcWikiID, dstWikiID, wikiGraph, threshold)
    case "w2v" =>

    case _ => throw new IllegalArgumentException("%s is not a valid subGraph generation techinque.".format(subGraph))
  }

}
