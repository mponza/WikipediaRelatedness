package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.clique

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness

class SubGraphRelatedness(options: RelatednessOptions) extends Relatedness {
  val subGraph = TopK.make(options)
  val weights = Relatedness..


  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = {

    topKsrc
    topKdst



    val graph = new WikiJungCiqueGraph()

    val cosimranker =
  }
}
