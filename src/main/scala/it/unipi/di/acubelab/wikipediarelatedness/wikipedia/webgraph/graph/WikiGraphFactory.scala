package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph

import it.unipi.di.acubelab.wikipediarelatedness.utils.OldConfiguration


object WikiGraphFactory {
  lazy val outGraph = new WikiGraph(OldConfiguration.wikipedia("outBVGraph"))
  lazy val inGraph = new WikiGraph(OldConfiguration.wikipedia("inBVGraph"))
  lazy val symGraph = new WikiGraph(OldConfiguration.wikipedia("symBVGraph"))
  lazy val noLoopSymGraph = new WikiGraph(OldConfiguration.wikipedia("noLoopSymBVGraph"))


  def makeWikiGraph(graphName: String) : WikiGraph = graphName match {
    case "outGraph" => outGraph
    case "inGraph" => inGraph
    case "symGraph" => symGraph
    case "noLoopSymGraph" => noLoopSymGraph
  }

}