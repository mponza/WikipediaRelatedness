package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk

import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.neural.W2VTopK


object TopKFactory {

  def make(name: String) = name match {
    case "esa" => new ESATopK
    case "esaentity" => new ESAEntityTopK


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
