package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.set

import it.unipi.di.acubelab.wikipediarelatedness.options.LocalClusteringOptions
import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.triangles.LocalClustering
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.utils.EntityVector
import org.slf4j.LoggerFactory

class LocalClusteringRelatedness(val options: LocalClusteringOptions) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[LocalClusteringRelatedness])

  val lc = new LocalClustering


  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {

    val srcLCvector = localClusteringVector(srcWikiID)
    val dstLCvector = localClusteringVector(dstWikiID)

    Similarity.cosineSimilarity(srcLCvector, dstLCvector)
  }


  /**
    *
    * @param wikiID
    * @return Vector of wikiIDs weighted by their local clustering coefficient.
    */
  def localClusteringVector(wikiID: Int): List[Tuple2[Int, Float]] = {
    val wikiVector = EntityVector.make(wikiID, options.vectorizer)

    wikiVector.map(wID => (wID, lc.getCoefficient(wID))).toList
  }
}