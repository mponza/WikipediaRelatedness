package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph

import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration


object WikiGraphFactory {
  lazy val outGraph = new WikiGraph(Configuration.wikipedia("outBVGraph"))
  lazy val inGraph = new WikiGraph(Configuration.wikipedia("inBVGraph"))
  lazy val symGraph = new WikiGraph(Configuration.wikipedia("symBVGraph"))
  lazy val noLoopSymGraph = new WikiGraph(Configuration.wikipedia("noLoopSymBVGraph"))

  def wikiBVGraph(graphName: String) : WikiGraph = graphName match {
    case "outGraph" => outGraph
    case "inGraph" => inGraph
    case "symGraph" => symGraph
    case "noLoopSymGraph" => noLoopSymGraph
  }
}