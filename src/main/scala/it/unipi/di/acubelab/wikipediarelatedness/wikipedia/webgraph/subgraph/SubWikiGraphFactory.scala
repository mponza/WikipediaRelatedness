package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.subgraph

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.subgraph.topk.context._
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.subgraph.topk.{DeepWalkSubWikiBVGraph, ESASubWikiBVGraph, W2VSubWikiBVGraph}


object SubWikiGraphFactory {

  def make(subGraph: String, srcWikiID: Int, dstWikiID: Int,
           wikiGraphName: String = "outGraph", threshold : Int = 1000) = {

    val wikiGraph = WikiBVGraphFactory.makeWikiGraph(wikiGraphName)

    subGraph match {

      case "neigh" | "neighborhood" => new NeighSubWikiBVGraph(srcWikiID, dstWikiID, wikiGraph)

      case "esa" => new ESASubWikiBVGraph(srcWikiID, dstWikiID, wikiGraph, threshold)
      case "w2v" => new W2VSubWikiBVGraph(srcWikiID, dstWikiID, wikiGraph, threshold)
      case "dw" => new DeepWalkSubWikiBVGraph(srcWikiID, dstWikiID, wikiGraph, threshold)

      case "cxt-w2v" => new W2VCxtSubWikiBVGraph(srcWikiID, dstWikiID, wikiGraph, threshold)
      case "cxt-dw" => new DeepWalkCxtSubWikiBVGraph(srcWikiID, dstWikiID, wikiGraph, threshold)

      case "pure-cxt-w2v" => new PureCxtW2VSubWikiBVGraph(srcWikiID, dstWikiID, wikiGraph, threshold)
      case "pure-cxt-dw" => new PureCxtDeepWalkSubWikiBVGraph(srcWikiID, dstWikiID, wikiGraph, threshold)

      case _ => throw new IllegalArgumentException("%s is not a valid subGraph generation techinque.".format(subGraph))
    }
  }

}
