package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph

import edu.uci.ics.jung.graph.{DirectedGraph, DirectedSparseGraph}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.weight.RelatednessWeights
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness


class WikiJungCliqueGraph(nodes: Seq[Int], relatedness: Relatedness) extends WikiJungGraph{

  override val graph = generateClique(nodes)
  override val weights = new RelatednessWeights(graph, relatedness)


  protected def generateClique(nodes: Seq[Int]) : DirectedGraph[Int, Long] = {
    val graph = new DirectedSparseGraph[Int, Long]

    nodes.foreach(graph.addVertex)

    // Each edge is a long with src and dst
    nodes.foreach {
      case src =>
        val srcShifted = src.asInstanceOf[Long] << 32

        nodes.filter(_ != src).foreach {
          case dst =>

            val edge = srcShifted | dst
            graph.addEdge(edge, src, dst)

        }

    }

    graph
  }


}
