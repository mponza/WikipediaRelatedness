package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.similarity

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph.WikiJungGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessOptions


trait SimRanker {

  def similarity(srcWikiID: Int, dstWikiID: Int, wikiJungGraph: WikiJungGraph): Double
}


object SimRanker {

  def make(options: RelatednessOptions) = options.simRanker match {

   case "csr" => new CoSimRanker(options.iterations, options.pprAlpha, options.csrDecay)
   case "ppr" => new PPRRanker(options.iterations, options.pprAlpha)

  }

}