package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.neural

import org.slf4j.LoggerFactory


class W2VTopK(w2vModelPath: String, w2vCachePath: String) extends NeuralTopK {
  override protected def logger = LoggerFactory.getLogger(getClass)

  override protected def modelPath: String = w2vModelPath
  override protected def cachePath: String = w2vCachePath
}