package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.neural.W2VTopK


object TopKFactory {

  def make(name: String) = name match {
    case "esa" => new ESATopK

    case "sg" | "dwsg" => W2VTopK.make(name)
  }
}
