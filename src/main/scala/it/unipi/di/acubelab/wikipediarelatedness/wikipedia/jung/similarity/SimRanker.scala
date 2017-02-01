package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.similarity

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph.WikiJungGraph


trait SimRanker {

  def similarity(srcWikiID: Int, dstWikiID: Int, graph: WikiJungGraph) : Float
}


object SimRankerFactory {

  //def make(options: Relate) = name match {
  //  case name => new CoSimRanker()

  //}

}