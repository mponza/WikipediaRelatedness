package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topkcontext

import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topkcontext.neural.W2VTopKContext


object TopKContextFactory {



  def make(name: String) = name match {

    case "esa" => new ESATopKContext


    //
    // Neural
    case "corpus" => new W2VTopKContext(
      Config.getString("wikipedia.neural.w2v.corpus"),
      Config.getString("wikipedia.cache.topk.neural.corpus.context")
    )

    case "sg" => new W2VTopKContext(
      Config.getString("wikipedia.neural.w2v.sg"),
      Config.getString("wikipedia.cache.topk.neural.sg.context")
    )

    case "dwsg" => new W2VTopKContext(
      Config.getString("wikipedia.neural.deepwalk.dwsg"),
      Config.getString("wikipedia.cache.topk.neural.dwsg.context")
    )

    case "dw10" => new W2VTopKContext(
      Config.getString("wikipedia.neural.deepwalk.dw10"),
      Config.getString("wikipedia.cache.topk.neural.dw10.context")
    )

  }

}
