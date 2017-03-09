package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.similarity

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.ESATopK


/**
  * WikiWalk implementation, namely PPRCos with the preference vector biased to ESA's concept vector.
  *
  * @param iterations
  * @param alpha
  */
class WikiBVWikiWalk(iterations: Int, alpha: Float) extends WikiBVPPRCos(iterations, alpha) {
  protected val esaTopK = new ESATopK


  /**
    * Default uniform preference vector.
    *
    * @param wikiID
    * @return
    */
  override protected def getPreferenceVector(wikiID: Int): Array[Double]= {
    val preference = Array.ofDim[Double]( wikiBVgraph.numNodes() )

    val topk = esaTopK.topKScoredEntities(wikiID, 10000)
    val sumScores = topk.map(_._2).sum

    topk.foreach {
      case (wID: Int, score: Float) =>
        val nodeID = wikiBVgraph.getNodeID(wikiID)
        val normScore = score / sumScores

        preference(nodeID) = normScore
    }

    preference
  }
}
