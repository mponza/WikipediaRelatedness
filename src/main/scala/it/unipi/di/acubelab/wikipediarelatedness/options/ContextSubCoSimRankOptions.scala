package it.unipi.di.acubelab.wikipediarelatedness.options

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessFactory

class ContextSubCoSimRankOptions(json: Option[Any] = None) extends CoSimRankOptions(json) {
  val subGraph = getString("subGraph", "cxt-w2v")      // technique for subgraph generation
  val threshold = getInt("threshold", 1000)                   // in case of sub-graph generation via ESA
  val wikiGraphName = getString("wikiGraph", "outGraph")     // super-WikipediaGraph
  val weighting = RelatednessFactory.make(getString("weighting"))


  override def toString() : String = {
    "%s,subGraph:%s,weighting:%s,wikiGraph:%s,threshold:%d".formatLocal(java.util.Locale.US,
      super.toString(),
      subGraph,
      weighting,
      wikiGraphName,
      threshold
    )
  }
}
