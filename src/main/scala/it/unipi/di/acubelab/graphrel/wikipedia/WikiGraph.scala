package it.unipi.di.acubelab.graphrel.wikipedia

import it.unimi.dsi.fastutil.io.BinIO
import it.unipi.di.acubelab.graphrel.utils.Configuration
import it.unipi.di.acubelab.graphrel.wikipedia.processing.webgraph.WikiBVGraph


object WikiGraph {
  lazy val outGraph = new WikiBVGraph(Configuration.wikipedia("outBVGraph"))
  lazy val inGraph = new WikiBVGraph(Configuration.wikipedia("inBVGraph"))
  lazy val symGraph = new WikiBVGraph(Configuration.wikipedia("symBVGraph"))
  lazy val noLoopSymGraph = new WikiBVGraph(Configuration.wikipedia("noLoopSymBVGraph"))

  lazy val distances = BinIO.loadObject(Configuration.wikipedia("symDistances"))
                        .asInstanceOf[Map[Tuple2[Int, Int], Int]]

  def wikiBVGraph(graphName: String) : WikiBVGraph = graphName match {
    case "outGraph" => outGraph
    case "inGraph" => inGraph
    case "symGraph" => symGraph
    case "noLoopSymGraph" => noLoopSymGraph
  }
}