package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms.utils.weighting

import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph.JungWikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

/**
  * Similar to JungEdgeWeights, but here weights are 1 / relatedness (useful for shortest path computation).
  * It also allows to mark edges and nodes as removed (by assigning to the corresponding edge the highest double value).
  *
  * @param relatedness
  * @param jungWikiGraph
  */
class JungInvertedEdgeWeights(relatedness: Relatedness, jungWikiGraph: JungWikiGraph)
  extends JungEdgeWeights(relatedness, jungWikiGraph) {

  override val logger  = LoggerFactory.getLogger(classOf[JungInvertedEdgeWeights])
  val removedNodes = new IntOpenHashSet  // nodes of which edges (-> and <-) have Double.MaxValue value
  val removedEdges = new ObjectOpenHashSet[String]


  /**
    * If the edge or one of the two nodes have been removed it returns Double.MaxValue.
    * Otherwise it returns the edge weight.
    *
    * @param edge
    * @return
    */
  override def transform(edge: String) : java.lang.Double = {
    if(removedEdges.contains(edge)) return Double.MaxValue

    // Nodes has already been removed
    val (src, dst) = nodesFromEdge(edge)
    if(removedNodes.contains(src) || removedNodes.contains(dst)) return Double.MaxValue


    cache.getDouble(edge)
  }


  /**
    * Computes all weights between wikiID and its successors in graph. Weights are 1-norm normalized.
    * Cache is updated.
    * Different from  the super method weights are 1 / relatedness if relatedness is not zero, zero otherwise.
    * Zero are used to filter edges and nodes by the computeCache procedure.
    *
    * @param wikiID
    */
  protected override def computeEdgeWeights(wikiID: Int) = {
    var norm1 = 0f
    val rels = ListBuffer.empty[Tuple2[Int, Float]]

    // Computes relatednesses and norm1.
    import scala.collection.JavaConversions._
    jungWikiGraph.graph.getSuccessors(wikiID).foreach {
      case nodeWikiID =>
        val rel = relatedness.computeRelatedness(wikiID, nodeWikiID)

        if (rel.isNaN) throw new IllegalArgumentException("NaN Relatedness while weighting graph")

        if (rel != 0f) {

          val invRel = 1 / rel
          norm1 += invRel
          rels += Tuple2(nodeWikiID, invRel)

        } else {
          rels += Tuple2(nodeWikiID, 0f)
        }
    }

    // Updates cache with normalized realtedness between wikiID and its successors.
    if (norm1 == 0f) norm1 = 1f
    rels.foreach {
      case (nodeWikiID, rel) => cache.putIfAbsent("%d->%d".format(wikiID, nodeWikiID), rel.toDouble / norm1)
    }
  }


  /*
   * Marks with Double.MaxValue edges and add to removedNodes nodes which belong to edges.
   */
  def removeNodes4ShortestPath(edges: List[String], srcWikiID: Int, dstWikiID: Int) = {
    edges.foreach {
      case edge =>
        removedEdges.add(edge)

        val (src, dst) = nodesFromEdge(edge)
        if (src != srcWikiID) removedNodes.add(src)
        if (dst != dstWikiID) removedNodes.add(dst)
    }
  }


  def nodesFromEdge(edge: String) = {
    val src = edge.split("->")(0).toInt
    val dst = edge.split("->")(1).toInt

    (src, dst)
  }

  /**
    * Unmark removed edges and nodes. Useful for multiple computation upon the same graph.
    */
  def cleanRemoved() = {
    removedEdges.clear()
    removedNodes.clear()
  }

}