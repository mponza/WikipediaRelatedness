package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.weight

import java.lang.Double

import edu.uci.ics.jung.graph.DirectedGraph
import org.apache.commons.collections15.Transformer


/**
  * Given a graph, it build a Transformer that uniformly weights all edges of all nodes.
  *
  * @param graph
  */
class UniformWeights(graph: DirectedGraph[Int, String]) extends Transformer[String, java.lang.Double] {

  protected val weights = weightGraph(graph)


  /**
    * Returns an array where the i-th element represents the uniform probabilty between i TO (->) its neighbors.
    *
    *   weight(i) = 1 / outdegree(i)
    *
    * @param graph
    * @return
    */
  protected def weightGraph(graph: DirectedGraph[Int, String]) = {

    val weights = Array.ofDim[Double](graph.getVertexCount)

    for (wikiID <- graph.getVertices) {
      if (graph.degree(wikiID) == 0) weights(wikiID) = 0.0
      else weights(wikiID) = 1 / graph.degree(wikiID).toDouble
    }

    weights
  }


  override def transform(edge: String): Double = {
    val src = edge.split("->")(0).toInt
    weights(src)
  }
}
