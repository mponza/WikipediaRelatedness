package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph.topk.{ESASubNodeCreator, NeuralSubNodeCreator}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.TopKFactory


object SubNodeCreatorFactory {

  def make(name: String, size: Int) = name match {
    case "esa" => new ESASubNodeCreator(size)

    case "sg" | "dwsg" => new NeuralSubNodeCreator(size, TopKFactory.make(name))
  }
}
