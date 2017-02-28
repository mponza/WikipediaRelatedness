package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph

import edu.uci.ics.jung.graph.{DirectedGraph, DirectedSparseGraph}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.weight.RelatednessWeights
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory


/**       n1 - n4
  *     /         \
  * src - n2 - n5 - dst
  *     \         /
  *       n3 - n6
  *
  * @param src
  * @param dst
  * @param relatedness
  */

class WikiLayeredJungGraph(src: Int, dst: Int, srcNodes: Seq[Int], dstNodes: Seq[Int], relatedness: Relatedness) extends WikiJungGraph {
  protected val logger = LoggerFactory.getLogger(getClass)

  override val graph = generateLayerdGraph(src, dst, srcNodes, dstNodes)
  override val weights = new RelatednessWeights(graph, relatedness)


  protected def generateLayerdGraph(src: Int, dst: Int, srcNodes: Seq[Int], dstNodes: Seq[Int]) : DirectedGraph[Int, Long] = {
    // logger.debug("Generating graph clique...")

    val graph = new DirectedSparseGraph[Int, Long]

    srcNodes.foreach(graph.addVertex)
    dstNodes.foreach(graph.addVertex)

    // Edges between src (dst) and its nodes.
    srcNodes.filter(_ != src).foreach( addUnidrectedEdge(src, _, graph) )
    dstNodes.filter(_ != dst).foreach( addUnidrectedEdge(dst, _, graph) )

    srcNodes.foreach {
      case srcNode =>
        dstNodes.foreach {
          case dstNode =>

            // srcNode (resp. dstNode) are:
            //    - different
            //    - neither src nor dst
            if (srcNode != dstNode &&
                srcNode != src && srcNode != dst &&
                dstNode != dst && dstNode != src) {

              addUnidrectedEdge(srcNode, dstNode, graph)
            }
        }
    }

    logger.debug("LayeredGraph generated with %d nodes".format(graph.getVertexCount))
    logger.debug("LayeredGraph generated with %d edges".format(graph.getEdgeCount))

    graph
  }

}
