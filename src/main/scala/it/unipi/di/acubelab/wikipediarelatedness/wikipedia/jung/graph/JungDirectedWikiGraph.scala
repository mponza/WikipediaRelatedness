package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph

import edu.uci.ics.jung.graph.{DirectedSparseGraph, Graph, UndirectedSparseGraph}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraph
import org.slf4j.LoggerFactory


class JungDirectedWikiGraph(wikiGraph: WikiGraph) extends JungWikiGraph(wikiGraph) {
  override def logger = LoggerFactory.getLogger(classOf[JungWikiGraph])

  override def generateJungGraph(wikiGraph: WikiGraph) : Graph[Int, String] = {
    logger.info("Building Jung Graph...")

    val graph = new DirectedSparseGraph[Int, String]()

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


    graph
  }

}
