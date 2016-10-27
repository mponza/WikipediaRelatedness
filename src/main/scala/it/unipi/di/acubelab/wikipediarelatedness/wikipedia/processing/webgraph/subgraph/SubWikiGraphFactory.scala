package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.subgraph

object SubWikiGraphFactory {
  def make(subGraph: String, srcWikiID: Int, dstWikiID: Int) = subGraph match {

    case "neigh" | "neighborhood" => new NeighSubWikiGraph(srcWikiID, dstWikiID)
    case "esa" => new ESASubWikiGraph(srcWikiID, dstWikiID)

    case _ => throw new IllegalArgumentException("%s is not a valid subGraph generation techinque.".format(subGraph))
  }
}
