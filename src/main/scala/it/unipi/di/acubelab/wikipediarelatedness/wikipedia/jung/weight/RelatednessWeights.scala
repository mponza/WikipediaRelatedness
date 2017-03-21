package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.weight

import edu.uci.ics.jung.graph.DirectedGraph
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap
import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.apache.commons.collections15.Transformer
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._


/**
  *
  *
  * @param graph
  * @param relatedness
  */
class RelatednessWeights(graph: DirectedGraph[Int, Long], relatedness: Relatedness)
                          extends Transformer[Long, java.lang.Double] {
  protected val logger = LoggerFactory.getLogger(getClass)
  val rawWs = new Long2DoubleOpenHashMap()
  val normSums = new Int2DoubleOpenHashMap()
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

      if (rel.isNaN) {
        logger.warn("NaN relatedness between %d and %d. Using 0.0 as weight.".format(src, dst))
        weights.put(edge, 0.0)

        rawWs.put(edge, 0.0)

      } else {

        weights.put(edge, rel)

        rawWs.put(edge, rel)
      }

      val currentSum = sums.getOrDefault(src, 0.0)
      sums.put(src, currentSum + rel)
      normSums.put(src, currentSum + rel)
    }

    // Normalizes weights by the sum of the source
    val normalizedWeights = new Long2DoubleOpenHashMap()
    for(edge <- graph.getEdges) {

      val weight = weights.get(edge)

      val src = getSource(edge)
      // sum is 1 if src has no edges or it has only edges with weight 0
      val sum = if (sums.getOrDefault(src, 0.0) == 0.0) 1.0 else sums.get(src)
      //logger.info("Normalizeding edge %d %d of %1.3f with %1.3f" format (src, getDestination(edge), weight, sum) )
      normalizedWeights.put(edge, weight / sum)
    }

    normalizedWeights
  }


  override def transform(edge: Long): java.lang.Double = {
    if (!weights.containsKey(edge)) throw new IllegalArgumentException("Edge not present!")
    val w = weights.get(edge)
    w
  }

  def getSource(edge: Long) = (edge.toLong >>> 32).toInt
  def getDestination(edge: Long) = edge.toInt


}
