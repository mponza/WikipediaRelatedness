package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.set

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessOptions}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.SetOperations
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory


class PreferentialAttachmentRelatedness(val options: RelatednessOptions) extends Relatedness {

  protected val graph = WikiBVGraphFactory.make("sym")
  protected val operations = new SetOperations(graph)

  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = {
    val srcNeigh = graph.successorArray(srcWikiID).size
    val dstNeigh = graph.successorArray(dstWikiID).size

    srcNeigh * dstNeigh
  }

  override def toString() = "PreferentialAttachment"

}
