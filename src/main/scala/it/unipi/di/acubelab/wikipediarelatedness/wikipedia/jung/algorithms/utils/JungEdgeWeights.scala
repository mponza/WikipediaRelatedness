package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms.utils

import com.google.common.base.Function
import org.apache.commons.collections15.Transformer
import edu.uci.ics.jung.graph.Graph
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness

import scala.collection.mutable.ListBuffer

/**
  * Edge weighter with relatedness on graph.
  * @param relatedness
  * @param graph
  */
class JungEdgeWeights(val relatedness: Relatedness, val graph: Graph[Int, String]) extends Transformer[String, java.lang.Double] {

  protected val cache = new Object2DoubleOpenHashMap[String]()  // Normalized edge weights.


  /**
    * Compute edge weight between two Wikipedia IDs, normalized upon 1-norm.
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
    * @param wikiID
    */
  protected def computeEdgeWeights(wikiID: Int) = {
    var norm1 = 0f
    val rels = ListBuffer.empty[Tuple2[Int, Float]]

    // Computes relatednesses and norm1.
    import scala.collection.JavaConversions._
    graph.getSuccessors(wikiID).foreach {
      case nodeWikiID =>
        val rel = relatedness.computeRelatedness(wikiID, nodeWikiID)

        norm1 += rel
        rels += Tuple2(nodeWikiID, rel)
    }

    // Updates cache with normalized realtedness between wikiID and its successors.
    if (norm1 == 0f) norm1 = 1f
    rels.foreach {
      case (nodeWikiID, rel) => cache.putIfAbsent("%d->%d".format(wikiID, nodeWikiID), rel.toDouble / norm1)
    }
  }
}
