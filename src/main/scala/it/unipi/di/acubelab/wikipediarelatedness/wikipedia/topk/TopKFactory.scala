package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.neural.W2VTopK

object TopKFactory {

  def make(name: String) = {
    case "esa" => new ESATopK

    case "w2v" => W2VTopK.make("w2v")
    case "dwsg" => W2VTopK.make("dwsg")
  }
}
