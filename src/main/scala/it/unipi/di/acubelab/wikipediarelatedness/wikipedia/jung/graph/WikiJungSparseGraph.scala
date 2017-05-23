package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph

import edu.uci.ics.jung.graph.{DirectedGraph, DirectedSparseGraph}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.weight.RelatednessWeights
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory


/**
  * Graph where edges are drawn only from/to src and dst (no inner edges between nodes).
  *
  * @param src
  * @param dst
  * @param nodes
  * @param relatedness
  */
class WikiJungSparseGraph(src: Int, dst: Int, nodes: Seq[Int], relatedness: Relatedness) extends  WikiJungGraph {
  protected val logger = LoggerFactory.getLogger(getClass)

  override val graph = generateSparseGraph(src, dst, nodes)
  override val weights = new RelatednessWeights(graph, relatedness)


  protected def generateSparseGraph(src: Int, dst: Int, nodes: Seq[Int]): DirectedGraph[Int, Long] = {
    val graph = new DirectedSparseGraph[Int, Long]()

    nodes.foreach(graph.addVertex)

    nodes.foreach {
      case node =>
        if (node != src) addUnidrectedEdge(src, node, graph)
        if (node != dst) addUnidrectedEdge(dst, node, graph)
    }

    //logger.debug("SparseGraph generated with %d nodes".format(graph.getVertexCount))
    //logger.debug("SparseGraph generated with %d edges".format(graph.getEdgeCount))

    graph
  }

}