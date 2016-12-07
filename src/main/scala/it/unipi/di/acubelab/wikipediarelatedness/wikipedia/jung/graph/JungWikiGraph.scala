package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraph
import edu.uci.ics.jung.graph.Graph
import org.slf4j.Logger


/**
  * Creates a Jung Graph from a WikiGraph.
  *
  * @param wikiGraph
  */
abstract class JungWikiGraph(val wikiGraph: WikiGraph) {

  val graph = generateJungGraph(wikiGraph)

  def logger: Logger

  def generateJungGraph(wikiGraph: WikiGraph) : Graph[Int, String]

  protected def getEdgeID(node1: Int, node2: Int) = "%d->%d".format(node1, node2)
}
