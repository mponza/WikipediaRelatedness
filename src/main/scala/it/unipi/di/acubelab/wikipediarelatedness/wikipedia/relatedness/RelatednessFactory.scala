package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.esa.ESARelatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.latent.{GraphSVDRelatedness, LDARelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.neural.{LINERelatedness, NeuralRelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank.subgraph.JungCoSimRankRelatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank.{CoSimRankRelatedness, PPRCosRelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.set.{JaccardRelatedness, LocalClusteringRelatedness, MilneWittenRelatedness}


object RelatednessFactory {


  def make(options: RelatednessOptions) : Relatedness = options.name.toLowerCase match {

    //
    // Set-based
    case "mw" | "milnewitten" => new MilneWittenRelatedness(options)
    case "jacc" | "jaccard" =>   new JaccardRelatedness(options)
    case "lc" | "localclustering" => new LocalClusteringRelatedness(options)


    //
    // Neural
    case "neural" => new NeuralRelatedness(options)


    //
    // ESA
    case "esa" => new ESARelatedness(options)
  }

}