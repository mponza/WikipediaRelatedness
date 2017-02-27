package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph.topkcontext

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph.SubNodeCreator
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topkcontext.TopKContext


abstract class TopKContextSubNodeCreator(size: Int) extends SubNodeCreator {
  protected def topKContext: TopKContext

  override def subNodes(srcWikiID: Int, dstWikiID: Int): Seq[Int] = topKContext.topKEntities(srcWikiID, dstWikiID, size)

}
