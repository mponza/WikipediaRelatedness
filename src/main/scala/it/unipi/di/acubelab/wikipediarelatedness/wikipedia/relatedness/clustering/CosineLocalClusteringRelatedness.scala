package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.clustering

import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessOptions
import org.slf4j.LoggerFactory


class CosineLocalClusteringRelatedness(options: RelatednessOptions) extends LocalClusteringRelatedness(options) {
  override val logger = LoggerFactory.getLogger(getClass)

  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    if (srcWikiID == dstWikiID) return 1f

    val srcVec = weightedNeighborhood(srcWikiID)
    val dstVec = weightedNeighborhood(dstWikiID)

    Similarity.cosineSimilarity(srcVec, dstVec)
  }


  /**
    * Returns the neighorhood of wikiID as vector of nodeIDs weighted with their local clustering coefficients.
    *
    */
  def weightedNeighborhood(wikiID:Int) : Seq[Tuple2[Int, Float]]= {
    val nodeIDs = graph.successorArray(wikiID)
    nodeIDs.map(nodeID => (nodeID, lc.getNodeCoefficient(nodeID))).toSeq
  }


  override def toString(): String = "CosineLocalClustering_graph:%s".format(options.graph)
}