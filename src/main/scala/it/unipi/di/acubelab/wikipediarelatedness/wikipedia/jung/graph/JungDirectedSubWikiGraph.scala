package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph

import edu.uci.ics.jung.graph.{DirectedSparseGraph, DirectedSparseMultigraph, Graph}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraph
import org.slf4j.LoggerFactory


/**
  * Generate a directed JungGraph from the WikiGraph where edges are added twice.
  * Edges will have the same original weight (relatedness) but different normalization factor.
 *
  * @param wikiGraph
  */
class JungDirectedSubWikiGraph(wikiGraph: WikiBVGraph) extends JungWikiGraph(wikiGraph) {
  override def logger = LoggerFactory.getLogger(classOf[JungDirectedSubWikiGraph])


  override def generateJungGraph(wikiGraph: WikiBVGraph) : Graph[Int, String] = {
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
          addEdge2Graph(graph, wikiID, wikiSucc)
          addEdge2Graph(graph, wikiSucc, wikiID)
      }
    }
    logger.info("Jung Graph with %d edges".format(graph.getEdgeCount))

    // JungWikiGraph.save2GraphML(graph)

    graph
  }

}
