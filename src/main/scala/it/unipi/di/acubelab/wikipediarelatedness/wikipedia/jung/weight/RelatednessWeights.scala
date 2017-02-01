package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.weight

import edu.uci.ics.jung.graph.DirectedGraph
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap
import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.apache.commons.collections15.Transformer
import scala.collection.JavaConversions._


/**
  *
  *
  * @param graph
  * @param relatedness
  */
class RelatednessWeights(graph: DirectedGraph[Int, Long], relatedness: Relatedness)
                          extends Transformer[Long, java.lang.Double] {

  protected val weights = computeNormalizedWeights()

  /**
    * Computes the normalized weights for graph by using relatedenss as raw weights.
    *
    * @return
    */
  protected def computeNormalizedWeights(): Long2DoubleOpenHashMap = {
    val weights = new Long2DoubleOpenHashMap()
    val sums = new Int2DoubleOpenHashMap()  // for normalization

    // Weights each edge with relatedness
    for(edge <- graph.getEdges) {

      val src = getSource(edge)
      val dst = getDestination(edge)

      val rel = relatedness.computeRelatedness(src, dst)
      weights.put(edge, rel)

      val currentSum = sums.getOrDefault(src, 0.0)
      sums.put(src, currentSum + rel)
    }

    // Normalizes weights by the sum of the source
    val normalizedWeights = new Long2DoubleOpenHashMap()
    for(edge <- graph.getEdges) {

      val weight = weights.get(edge)

      val src = getSource(edge)
      // sum is 1 if src has no edges or it has only edges with weight 0
      val sum = if (sums.getOrDefault(src, 0.0) == 0.0) 1.0 else sums.get(src)

      normalizedWeights.put(edge, weight / sum)
    }

    normalizedWeights
  }


  override def transform(edge: Long): java.lang.Double = {
    if (!weights.containsKey(edge)) throw new IllegalArgumentException("Edge not present!")
    val w = weights.get(edge)
    w
  }

  protected def getSource(edge: Long) = (edge.toLong >>> 32).toInt
  protected def getDestination(edge: Long) = edge.toInt


}
