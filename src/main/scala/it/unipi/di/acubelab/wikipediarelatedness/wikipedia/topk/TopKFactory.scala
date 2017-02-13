package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk

import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.neural.W2VTopK


object TopKFactory {

  def make(name: String) = name match {

    //
    // ESA
    case "esa" => new ESATopK
    case "esaentity" => new ESAEntityTopK

    //
    // Neural
    case "corpus" => new W2VTopK(
      Config.getString("wikipedia.neural.w2v.corpus"),
      Config.getString("wikipedia.cache.topk.neural.corpus.entity2entities")
    )

    case "sg" => new W2VTopK(
      Config.getString("wikipedia.neural.w2v.sg"),
      Config.getString("wikipedia.cache.topk.neural.sg.entity2entities")
    )

    case "dwsg" => new W2VTopK(
      Config.getString("wikipedia.neural.deepwalk.dwsg"),
      Config.getString("wikipedia.cache.topk.neural.dwsg.entity2entities")
    )


    //
    // MilneWitten
    case "mw.in" => new MilneWittenTopK("in")
    case "mw.out" => new MilneWittenTopK("out")
    case "mw.sym" => new MilneWittenTopK("sym")
  }

}
