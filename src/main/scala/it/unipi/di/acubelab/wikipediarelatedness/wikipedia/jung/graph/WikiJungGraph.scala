package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph

import edu.uci.ics.jung.graph.{DirectedGraph, DirectedSparseGraph}
import org.apache.commons.collections15.Transformer


/**
  * Directed Jung Graph of Wikipedia Entities, properly weighted.
  *
  */
trait WikiJungGraph {

  def graph:  DirectedGraph[Int, Long]
  def weights: Transformer[Long, java.lang.Double]



  /**
    * Adds an edge from src to dst and from dst to src in graph.
    *
    * @param src
    * @param dst
    * @param graph
    */
  protected def addUnidrectedEdge(src: Int, dst: Int, graph: DirectedSparseGraph[Int, Long]): Unit = {
    // src -> dst
    val srcShifted = src.asInstanceOf[Long] << 32
    val srcNodeEdge = srcShifted | dst
    graph.addEdge(srcNodeEdge, src, dst)

    // dst -> src
    val dstShifted = dst.asInstanceOf[Long] << 32
    val dstSrcEdge = dstShifted | src
    graph.addEdge(dstSrcEdge, dst, src)
  }
}


