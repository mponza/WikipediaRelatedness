package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.clustering.{CosineLocalClusteringRelatedness, JaccardLocalClusteringRelatedness, LocalClusteringRelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.esa.{ESAEntityRelatedness, ESARelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.set.{JaccardRelatedness, MilneWittenRelatedness}


object RelatednessFactory {


  def make(options: RelatednessOptions) : Relatedness = options.name.toLowerCase match {

    //
    // Set-based
    case "milnewitten" => new MilneWittenRelatedness(options)
    case "jaccard" =>   new JaccardRelatedness(options)

    //
    // LocalClustering-based
    case "jaccardlocalclustering" => new JaccardLocalClusteringRelatedness(options)
    case "cosinelocalclustering" => new CosineLocalClusteringRelatedness(options)

    //
    // ESA
    case "esa" => new ESARelatedness(options)
    case "esaentity" => new ESAEntityRelatedness(options)

    //
    // Neural
    //case "neural" => new NeuralRelatedness(options)



  }

}