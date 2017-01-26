package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph

import java.io.{BufferedWriter, FileWriter, PrintWriter}

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraph
import edu.uci.ics.jung.graph.Graph
import edu.uci.ics.jung.io
import org.apache.commons.collections15.Transformer
import org.slf4j.Logger


/**
  * Creates a Jung Graph from a WikiGraph.
  *
  * @param wikiGraph
  */
abstract class JungWikiGraph(val wikiGraph: WikiBVGraph) {

  val graph = generateJungGraph(wikiGraph)

  def logger: Logger

  def generateJungGraph(wikiGraph: WikiBVGraph) : Graph[Int, String]

  protected def getEdgeID(node1: Int, node2: Int) = "%d->%d".format(node1, node2)


  /**
    * Adds edge src->dst to graph if not present.
    *
    * @param graph
    * @param src
    * @param dst
    * @return
    */
  protected def addEdge2Graph(graph: Graph[Int, String], src: Int, dst: Int) = {
    val edgeID = getEdgeID(src, dst)

    if(!graph.containsEdge(edgeID)) {
      graph.addEdge(edgeID, src, dst)
    }
  }


  /**
    * Computes shortestPath from src to dst where edges are weighted with weights.
 *
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


  /**
    * Removes edges and then those vertices with outdegree equals to 0.
 *
    * @param edges
    */
  def removeEdges(edges: Seq[String]) = {
    import scala.collection.JavaConversions._

    logger.info("Removing edges...")
    // Remove edges.
    edges.foreach(graph.removeEdge)

    logger.info("Removing vertices...")
    // Subsequently remove vertices.
    val zeros = graph.getVertices.filter(graph.degree(_) == 0)
    zeros.foreach(graph.removeVertex)

    logger.info("Cleaned graph: %d Vertices and %d Edges".format(graph.getVertexCount, graph.getEdgeCount))
  }

}


object JungWikiGraph {

  /**
    * Saves Jung Graph into graphml fileformat.
    * Adapted from https://halfclosed.wordpress.com/2010/12/04/graphml-with-jung-saving/
    *
    * @param jungGraph
    * @param path
    */
  def save2GraphML(jungGraph: Graph[Int, String], path: String = "/tmp/graph.graphml") = {
    println("Saving graph into %s...".format(path))

    val vertexTransformer = new Transformer[Int, String]() {
      def transform(wikiID: Int) = "%d".format(wikiID)
    }

    val graphWriter = new io.GraphMLWriter[Int, String]()
    val printer = new PrintWriter(new BufferedWriter(new FileWriter(path)))

    // graphWriter.addVertexData("x", "X", "0", vertexTransformer)
    // graphWriter.addVertexData("y", "Y", "0", vertexTransformer)

    graphWriter.save(jungGraph, printer)

    println("Graph saved!")
  }

}