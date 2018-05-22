package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.secondstage.weights

trait WeightsOfSubGraph {

  def weighting(srcWikiID: Int, dstWikiID: Int): Float

}
