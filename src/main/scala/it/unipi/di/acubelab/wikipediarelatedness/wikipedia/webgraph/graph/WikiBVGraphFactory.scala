package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph

import it.unipi.di.acubelab.wikipediarelatedness.utils.Config


/**
  * Factory object to have the wikiBVGraph from its name.
  *
  */
object WikiBVGraphFactory {
  lazy val outWikiBVGraph = new WikiBVGraph(Config.getString("wikipedia.webgraph.out"))
  lazy val inWikiBVGraph = new WikiBVGraph(Config.getString("wikipedia.webgraph.in"))
  lazy val symWikiBVGraph = new WikiBVGraph(Config.getString("wikipedia.webgraph.sym"))
  lazy val symNoLoopWikiBVGraph = new WikiBVGraph(Config.getString("wikipedia.webgraph.sym_no_loop"))


  /**
    * Returns WikiBVGraph from its name.
    *
    * @param graphName
    * @return
    */
  def makeWikiGraph(graphName: String) : WikiBVGraph = graphName match {
    case "out" => outWikiBVGraph
    case "in" => inWikiBVGraph
    case "sym" => symWikiBVGraph
    case "sym_no_loop" => symNoLoopWikiBVGraph
  }

}