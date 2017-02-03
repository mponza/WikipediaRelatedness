package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph.topk.ESASubNodeCreator


object SubNodeCreatorFactory {

  def make(name: String, size: Int) = name match {
    case "esa" => new ESASubNodeCreator(size)
  }
}