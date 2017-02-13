package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.ranker

trait WebGraphPageRanker {

  /**
    * Performs one step of PageRank and returns the ranking vector.
    *
    * @return
    */
  def rankingStep(): Array[Double]

}
