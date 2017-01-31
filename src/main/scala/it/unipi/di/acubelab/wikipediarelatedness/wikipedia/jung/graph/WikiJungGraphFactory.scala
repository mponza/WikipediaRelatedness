package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessFactory


object WikiJungGraphFactory {


  /**
    * Makes a WikiJungGraph out-directed and uniformly weighted.
    *
    * @return
    */
  def make(name: String) = name match {
    //case "bv" => new WikiJungBVGraph
    case "clique" =>
  }


  def make(op) = {

    val relatendess = RelatednessFactory.make()

  }
}
