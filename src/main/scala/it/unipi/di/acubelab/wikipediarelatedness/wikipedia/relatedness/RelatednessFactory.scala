package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.clustering.{CosineLocalClusteringRelatedness, JaccardLocalClusteringRelatedness, LocalClusteringRelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.esa.ESARelatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.set.{JaccardRelatedness, MilneWittenRelatedness}


object RelatednessFactory {


  def make(options: RelatednessOptions) : Relatedness = options.name.toLowerCase match {

    //
    // Set-based
    case "mw" | "milnewitten" => new MilneWittenRelatedness(options)
    case "jacc" | "jaccard" =>   new JaccardRelatedness(options)

    //
    // LocalClustering-based
    case "jlc" | "jaccardlocalclustering" => new JaccardLocalClusteringRelatedness(options)
    case "clc" | "cosinelocalclustering" => new CosineLocalClusteringRelatedness(options)


    //
    // Neural
    //case "neural" => new NeuralRelatedness(options)


    //
    // ESA
    case "esa" => new ESARelatedness(options)
  }

}