package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph

import edu.uci.ics.jung.graph.DirectedSparseGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.weight.UniformWeights
import org.apache.commons.collections15.Transformer
import org.slf4j.LoggerFactory


/**
  * Wikipedia weighted graph modeled with Jung. Weights are uniform by default.
  *
  * @param edges
  */
class WikiJungGraph(edges: Array[(Int, Int)]) {
  protected val logger = LoggerFactory.getLogger(getClass)

  val graph:  DirectedSparseGraph[Int, String] = buildGraph(edges)
  val weights: Transformer[String, java.lang.Double] = new UniformWeights(graph)


  /**
    * Builds a directed jung graph from a list of edges.
    *
    * @param edges
    * @return
    */
  protected def buildGraph(edges : Seq[(Int, Int)]) : DirectedSparseGraph[Int, String] = {
    logger.info("Building Jung Graph...")

    val graph = new DirectedSparseGraph[Int, String]()

    edges.foreach {
      case (src: Int, dst: Int) =>

        if(!graph.containsVertex(src)) graph.addVertex(src)
        if(!graph.containsVertex(dst)) graph.addVertex(dst)

        val edgeID = "%d->%d".format(src, dst)

        graph.addEdge(edgeID, src, dst)
    }

    logger.info("Jung Graph with %d nodes.".format(graph.getVertexCount))
    logger.info("Jung Graph with %d edges".format(graph.getEdgeCount))

    graph
  }

}
