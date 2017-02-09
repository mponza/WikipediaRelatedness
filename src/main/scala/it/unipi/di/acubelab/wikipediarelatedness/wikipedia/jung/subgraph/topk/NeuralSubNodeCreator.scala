package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph.topk
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.TopK
import org.slf4j.{Logger, LoggerFactory}


/**
  * SubNodes via neural network embeddings.
  *
  * @param size
  * @param neuralTopK
  */
class NeuralSubNodeCreator(size: Int, neuralTopK: TopK) extends TopKSubNodeCreator(size) {
  override protected def logger: Logger = LoggerFactory.getLogger(getClass)
  override protected val topK: TopK = neuralTopK
}
