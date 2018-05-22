package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.graph.WikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.graphstructure.MilneWittenRelatedness

object WikiRelatednessFactory {

  def makeMilneWitten(inGraphFilename: String) : WikiRelatedness = {
    new MilneWittenRelatedness( WikiGraph.apply(inGraphFilename) )
  }

}
