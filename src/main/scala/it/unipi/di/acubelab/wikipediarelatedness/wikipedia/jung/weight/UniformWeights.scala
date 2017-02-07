package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.weight

import java.lang.Double

import edu.uci.ics.jung.graph.DirectedGraph
import org.apache.commons.collections15.Transformer


/**
  * Given a graph, it build a Transformer that uniformly weights all edges of all nodes.
  *
  * @param graph
  */
class UniformWeights(graph: DirectedGraph[Int, Long]) extends Transformer[Long, java.lang.Double] {


  override def transform(edge: Long): Double = {
    val src = graph.getSource(edge)

    if (graph.degree(src) == 0) 0.0
    else 1.0 / graph.outDegree(src)
  }
}
