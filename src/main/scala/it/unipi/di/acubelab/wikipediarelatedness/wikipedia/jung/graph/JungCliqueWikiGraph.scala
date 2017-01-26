package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph

import org.slf4j.LoggerFactory
import edu.uci.ics.jung.graph.{DirectedSparseGraph, Graph}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraph

/**
  * Clique Jung Graph from a Wikipedia subgraph.
 *
  * @param wikiGraph
  */
class JungCliqueWikiGraph(wikiGraph: WikiBVGraph) extends JungWikiGraph(wikiGraph) {
  override def logger = LoggerFactory.getLogger(classOf[JungCliqueWikiGraph])

  override def generateJungGraph(wikiGraph: WikiBVGraph) : Graph[Int, String] = {
    logger.info("Building Jung Graph...")

    val graph = new DirectedSparseGraph[Int, String]()

    // Add vertices
    for(wikiID <- wikiGraph.getWikiIDs()) {
      graph.addVertex(wikiID)
    }
    logger.info("Jung Graph with %d nodes.".format(graph.getVertexCount))

    // Add edges (clique)
    for(src <- wikiGraph.getWikiIDs()) {
      for(dst <- wikiGraph.getWikiIDs()) {
        if(src != dst) {
          addEdge2Graph(graph, src, dst)
        }
      }
    }

    logger.info("Jung Graph with %d edges".format(graph.getEdgeCount))

    graph
  }
}
