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

    val topk = esaTopK.topKScoredEntities(wikiID, 10000).filter( x => wikiBVgraph.contains(x._1) )
    val sumScores = topk.map(_._2.toInt).sum.toDouble

    topk.foreach {
      case (wID: Int, score: Float) =>

        val nodeID = wikiBVgraph.getNodeID(wID)
        val normScore = score.toInt / sumScores

        preference(nodeID) = normScore.toDouble
    }


    preference
  }
}
