package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms.utils.weighting

import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.oldgraph.JungWikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.apache.commons.collections15.Transformer
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

/**
  * Edge weighter with relatedness on graph.
  * @param relatedness
  * @param jungWikiGraph
  */
class JungEdgeWeights(val relatedness: Relatedness, val jungWikiGraph: JungWikiGraph) extends Transformer[String, java.lang.Double] {
  val logger = getLogger()
  protected val cache = new Object2DoubleOpenHashMap[String]()  // Normalized edge weights.

  // Clean graph from edges with weight 0.0
  val toRemoveEdges = computeCache()
  jungWikiGraph.removeEdges(toRemoveEdges)


  def getLogger() = LoggerFactory.getLogger(classOf[JungEdgeWeights])


  /**
    * Compute edge weight between two Wikipedia IDs, normalized upon 1-norm.
    *
    * @param edge
    * @return
    */
  override def transform(edge: String) : java.lang.Double = {
    if (!cache.containsKey(edge)) {
      val src = edge.split("->")(0).toInt
      computeEdgeWeights(src)
    }

    cache.getDouble(edge)
  }


  /**
    * Computes all weights between wikiID and its successors in graph. Weights are 1-norm normalized.
    * Cache is updated.
    *
    * @param wikiID
    */
  protected def computeEdgeWeights(wikiID: Int) = {
    var norm1 = 0f
    val rels = ListBuffer.empty[Tuple2[Int, Float]]

    // Computes relatednesses and norm1.
    import scala.collection.JavaConversions._
    jungWikiGraph.graph.getSuccessors(wikiID).foreach {
      case nodeWikiID =>
        val rel = relatedness.computeRelatedness(wikiID, nodeWikiID)
        if(rel.isNaN) throw new IllegalArgumentException("NaN Relatedness while weighting graph")

        norm1 += rel
        rels += Tuple2(nodeWikiID, rel)

    }

    // Updates cache with normalized realtedness between wikiID and its successors.
    if (norm1 == 0f) norm1 = 1f
    rels.foreach {
      case (nodeWikiID, rel) => cache.putIfAbsent("%d->%d".format(wikiID, nodeWikiID), rel.toDouble / norm1)
    }

  }


  /**
    * Fill cache with edge weights. It returns edges to be removed (default with wegiht equals to 0.0).
    *
    * @return
    */
  def computeCache() : List[String] = {
    import scala.collection.JavaConversions._
    logger.info("Computing Weight Cache...")
    jungWikiGraph.graph.getEdges.filter(transform(_) == 0.0).toList
  }

}
