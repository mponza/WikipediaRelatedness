package it.unipi.di.acubelab.graphrel.wikipedia.relatedness

trait Relatedness {
  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Double
  def name() : String
}
