package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.similarity.matrix

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph.WikiJungGraph
import scala.collection.JavaConversions._


class LaplacianGraph(wikiJungGraph: WikiJungGraph) {

  // n x n matrix
  val (matrix, node2index, volume) = graph2matrix(wikiJungGraph)  // laplacian matrix and node mapping in its indices.


  /**
    * Creates the laplacian matrix and the mapping between nodeID and the matrix index.
    *
    * @param wikiJungGraph
    * @return
    */
  protected def graph2matrix(wikiJungGraph: WikiJungGraph) : (Array[Array[Double]], Int2IntOpenHashMap, Double)= {
    val n = wikiJungGraph.graph.getVertexCount
    val laplacian = Array.ofDim[Double](n, n)  // weighted laplacian from http://slideplayer.com/slide/5756785/
    var volume = 0.0

    // Mapping between node and matrix indices
    val node2index = new Int2IntOpenHashMap
    wikiJungGraph.graph.getVertices.zipWithIndex.foreach {
      case (wikiID: Int, index: Int) =>

        // not consider nodes with no out edges
        node2index.put(wikiID, index)

    }

    // Filling laplacian matrix
    wikiJungGraph.graph.getVertices.foreach {
      case src =>

        val srcIndex = node2index.get(src)
        var d = 0.0

        wikiJungGraph.graph.getOutEdges(src).foreach {
          case edge =>

            val dst = wikiJungGraph.graph.getDest(edge)

            val dstIndex = node2index(dst)

            val weight = wikiJungGraph.weights.transform(edge)
            laplacian(srcIndex)(dstIndex) = -weight

            volume += weight
            d += weight
        }

        // diagonal elements
        laplacian(srcIndex)(srcIndex) = 1.0
    }

    (laplacian, node2index, volume)
  }

}
