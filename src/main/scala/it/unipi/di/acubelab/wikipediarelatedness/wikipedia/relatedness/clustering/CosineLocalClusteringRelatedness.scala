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
    * Returns the neighborhood of wikiID as vector of nodeIDs weighted with their local clustering coefficients.
    *
    */
  def weightedNeighborhood(wikiID:Int) : Seq[Tuple2[Int, Float]]= {
    val wikiIDs = graph.successorArray(wikiID)

    val weightedWikiIDs = wikiIDs.map(wID => ( wID, lc.getCoefficient( wID ) )).toSeq

    if (options.threshold > 0) {
      thresholdVector(weightedWikiIDs, options.threshold)
    } else {
      weightedWikiIDs
    }
  }


  override def toString(): String = {
    if (options.threshold > 0) {
      "CosineLocalClustering_graph:%s,threshold:%d".format(options.graph, options.threshold)
    } else {
      "CosineLocalClustering_graph:%s".format(options.graph)
    }
  }
}