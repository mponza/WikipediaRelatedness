package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

trait WikiRelatedness {
  def relatedness(srcWikiID: Int, dstWikiID: Int): Float
}
