package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topkcontext.neural

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topkcontext.neural.NeuralTopKContext
import org.slf4j.LoggerFactory

class W2VTopKContext(w2vModelPath: String, w2vCachePath: String) extends NeuralTopKContext {

  override protected def logger = LoggerFactory.getLogger(getClass)
  override protected def modelPath: String = w2vModelPath
  override protected def cacheTopKContextPath: String = w2vCachePath

}