package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph.topkcontext
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topkcontext.{ESATopKContext, TopKContext}

class ESAContextSubNodeCreator(size: Int) extends TopKContextSubNodeCreator(size) {

  override protected def topKContext: TopKContext = new ESATopKContext
}
