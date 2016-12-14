package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms.utils.weighting

import it.unimi.dsi.fastutil.ints.{Int2DoubleOpenHashMap, IntOpenHashSet}
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph.JungWikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

/**
  * Similar to JungEdgeWeights, but here weights are 1 - relatedness (useful for shortest path computation).
  * It also allows to mark edges and nodes as removed (by assigning to the corresponding edge the highest double value).
  *
  * @param relatedness
  * @param jungWikiGraph
  */
class JungInvertedEdgeWeights(relatedness: Relatedness, jungWikiGraph: JungWikiGraph)
  extends JungEdgeWeights(relatedness, jungWikiGraph) {
  //val outSums = new Int2DoubleOpenHashMap()

  val removedNodes = new IntOpenHashSet()  // nodes of which edges (-> and <-) have Double.MaxValue value
  val removedEdges = new ObjectOpenHashSet[String]()

  override def getLogger() = LoggerFactory.getLogger(classOf[JungInvertedEdgeWeights])


  /**
    * If the edge or one of the two nodes have been removed it returns Double.MaxValue.
    * Otherwise it returns the edge weight.
    *
    * @param edge
    * @return
    */
  override def transform(edge: String) : java.lang.Double = {
    // != null because superclass constructor is called first
    if(removedEdges != null && removedEdges.contains(edge)) return Double.MaxValue

    // Nodes has already been removed
    val (src, dst) = nodesFromEdge(edge)
    if(removedNodes != null && (removedNodes.contains(src) || removedNodes.contains(dst))) return Double.MaxValue

    super.transform(edge)
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
        if (rel < 0 || rel > 1) throw new IllegalArgumentException("Relatedness out of range %1.10f".format(rel))

        if (rel != 0f) {
          val invRel = 1 - rel
          norm1 += invRel
          rels += Tuple2(nodeWikiID, invRel)

        } else {
          rels += Tuple2(nodeWikiID, -1f)
        }
    }

    // Updates cache with normalized realtedness between wikiID and its successors.
    //if (norm1 == 0f) norm1 = 1f
    //outSums.put(wikiID, norm1)
    rels.foreach {
      case (nodeWikiID, rel) => cache.putIfAbsent("%d->%d".format(wikiID, nodeWikiID), rel.toDouble / norm1)
    }
  }



  /**
    * Fill cache with edge weights. It returns edges to be removed.
    * Here < 0 because 0.0 now represents two equal nodes
    *
    * @return
    */
  override def computeCache() : List[String] = {
    import scala.collection.JavaConversions._
    logger.info("Computing Weight Cache...")
    jungWikiGraph.graph.getEdges.filter(transform(_) < 0).toList
  }


  /*
   * Marks with Double.MaxValue edges and add to removedNodes nodes which belong to edges.
   */
  def removeNodes4ShortestPath(edges: List[String], srcWikiID: Int, dstWikiID: Int) = {
    if (edges.nonEmpty)
      removedEdges.add(edges.last)
    /*edges.foreach {
      case edge =>
        removedEdges.add(edge)

        val (src, dst) = nodesFromEdge(edge)
        //if (src != srcWikiID) removedNodes.add(src)
        //if (dst != dstWikiID) removedNodes.add(dst)
    }*/
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