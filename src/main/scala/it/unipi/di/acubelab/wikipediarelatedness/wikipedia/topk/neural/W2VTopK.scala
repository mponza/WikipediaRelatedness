package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.neural

import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import org.slf4j.LoggerFactory


class W2VTopK(w2vModelPath: String, w2vCachePath: String) extends NeuralTopK {
  override protected def logger = LoggerFactory.getLogger(getClass)

  override protected def modelPath: String = w2vModelPath
  override protected def cachePath: String = w2vCachePath
}


object W2VTopK {

  def make(name: String): W2VTopK = name match {


    case "sg" => new W2VTopK(
                                Config.getString("wikipedia.neural.w2v.sg"),
                                Config.getString("wikipedia.cache.neural.sg.entity2entities")
                              )
    case "dwsg" => new W2VTopK(
                                Config.getString("wikipedia.neural.deepwalk.dwsg"),
                                Config.getString("wikipedia.cache.neural.dwsg.entity2entities")
                              )
  }

}