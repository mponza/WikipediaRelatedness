package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms.utils.weighting.JungInvertedEdgeWeights
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph.JungWikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

/**
  * Computes k disjoint shortest path between two nodes.
  *
  * @param jungWikiGraph
  * @param relatedness
  * @param k
  */
class JungKShortestPaths(jungWikiGraph: JungWikiGraph, relatedness: Relatedness, k: Int = 10) {
  val logger = LoggerFactory.getLogger(classOf[JungKShortestPaths])
  val weights = new JungInvertedEdgeWeights(relatedness, jungWikiGraph)

  /**
    * Returns top-k shortest path with the corresponding weighted nodes.
    * The i-th element of the returned list is a list of (WikiID, Weight) in the  jungWikiGraph.
    * Weight is the original relatedness.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return [ [(WikiID, Weight)] ]
    */
  def topKShortestPaths(srcWikiID: Int, dstWikiID: Int) = {
    import scala.collection.JavaConversions._

    val weightedPaths = ListBuffer.empty[List[Tuple2[Int, Float]]]  // [ [node paths from src to dst and their weights] ]

    for(i <- 0 until k) {
      val dijkstra = new DijkstraShortestPath[Int, String](jungWikiGraph.graph, weights, false)

      if(jungWikiGraph.graph.containsVertex(srcWikiID) && jungWikiGraph.graph.containsVertex(dstWikiID)) {

        val pathEdges = dijkstra.getPath(srcWikiID, dstWikiID)
        // [ ( WikiID, Relatedness (real) ) ] which represents the wieghted shortest path
        val nodeWeights = pathEdges.filter(weights.transform(_) != Double.MaxValue).map {
          case edge =>
            val src = edge.split("->")(0).toInt
            val dst = edge.split("->")(1).toInt

            val weight = relatedness.computeRelatedness(src, dst)

            Tuple2(dst, weight.toFloat)
        }.toList

        if(nodeWeights.nonEmpty) {
          weightedPaths += nodeWeights
        }

        // Removes (marks) edges and nodes
        weights.removeNodes4ShortestPath(pathEdges.toList, srcWikiID, dstWikiID)

      } else {
        logger.warn("%d and %d are not connect in the clique graph.".format(srcWikiID, dstWikiID))
      }
    }
    weights.cleanRemoved()

    weightedPaths.toList
  }

}