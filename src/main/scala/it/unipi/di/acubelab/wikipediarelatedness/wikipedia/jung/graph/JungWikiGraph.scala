package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraph
import edu.uci.ics.jung.graph.Graph
import org.apache.commons.collections15.Transformer
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


  /**
    * Computes shortestPath from src to dst where edges are weighted with weights.
    * @param src
    * @param dst
    * @param weights
    */
  def shortestDistance(src: Int, dst: Int, weights: Transformer[String, java.lang.Double]) = {
    val djkstra = new DijkstraShortestPath[Int, String](graph, weights)
    val distance = djkstra.getPath(src, dst).size()

    logger.info("Distance between %d and %d is %d".format(src, dst, distance))
    distance
  }
}