package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph.topkcontext

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topkcontext.TopKContext

import org.slf4j.{Logger, LoggerFactory}

class NeuralSubNodeCreator(size: Int, neuralTopKContext: TopKContext) extends TopKContextSubNodeCreator(size) {
  protected def logger: Logger = LoggerFactory.getLogger(getClass)
  protected val topKContext = neuralTopKContext
}
