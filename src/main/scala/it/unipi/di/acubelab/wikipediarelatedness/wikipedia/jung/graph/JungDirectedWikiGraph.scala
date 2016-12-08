package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph

import java.io.{BufferedWriter, FileWriter, PrintWriter}

import edu.uci.ics.jung.graph.{DirectedSparseGraph, DirectedSparseMultigraph, Graph, UndirectedSparseGraph}
import edu.uci.ics.jung.io
import edu.uci.ics.jung.io.GraphMLWriter
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms.utils.JungEdgeWeights
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.apache.commons.collections15.Transformer
import org.slf4j.LoggerFactory


class JungDirectedWikiGraph(wikiGraph: WikiGraph) extends JungWikiGraph(wikiGraph) {
  override def logger = LoggerFactory.getLogger(classOf[JungWikiGraph])


  override def generateJungGraph(wikiGraph: WikiGraph) : Graph[Int, String] = {
    logger.info("Building Jung Graph...")

    val graph = new DirectedSparseMultigraph[Int, String]()

    // Add vertices
    for(wikiID <- wikiGraph.getWikiIDs()) {
      graph.addVertex(wikiID)
    }
    logger.info("Jung Graph with %d nodes.".format(graph.getVertexCount))


    // Add edges
    for(wikiID <- wikiGraph.getWikiIDs()) {
      wikiGraph.wikiSuccessors(wikiID).foreach {

        case wikiSucc =>
          val edgeID = getEdgeID(wikiID, wikiSucc)
          graph.addEdge(edgeID, wikiID, wikiSucc)
      }
    }
    logger.info("Jung Graph with %d edges".format(graph.getEdgeCount))

    JungDirectedWikiGraph.save2GraphML(graph)

    graph
  }

}


object JungDirectedWikiGraph {

  /**
    * Saves Jung Graph into graphml fileformat.
    * Adapted from https://halfclosed.wordpress.com/2010/12/04/graphml-with-jung-saving/
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