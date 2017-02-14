package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph.topk._
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.TopKFactory


object SubNodeCreatorFactory {

  def make(name: String, size: Int) = name match {
    case "esa" => new ESASubNodeCreator(size)
    case "esaentity" => new ESAEntitySubNodeCreator(size)

    case "sg" | "dwsg" | "corpus" => new NeuralSubNodeCreator(size, TopKFactory.make(name))

    case "mw.in" => new MilneWittenSubNodeCreator("in", size)
    case "mw.out" => new MilneWittenSubNodeCreator("out", size)
    case "mw.sym" => new MilneWittenSubNodeCreator("sym", size)

    case "esamw.in" => new ESAMilneWittenSubNodeCreator("in", size)
    case "esamw.out" => new ESAMilneWittenSubNodeCreator("out", size)
    case "esamw.sym" => new ESAMilneWittenSubNodeCreator("sym", size)
  }
}
