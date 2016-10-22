package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.options._


object RelatednessFactory {
  /**
    * @param json Json string:
    *                      {
    *                          // Relatedness measure
    *                         "relatedness": MilneWitten/Jaccard/LLP/MultiLLP/w2v
    *
    *                         // Wikipedia graph to be used
    *                         "graph": "in", "out", "sym", "noloopsym"
    *                      }
    * @return Relatedness object, instatiated with the specified parameters.
    */
  def make(json: Option[Any]) : Relatedness = {
    val relatednessName = getRelatednessName(json)

    relatednessName match {
      case "MilneWitten" => new MilneWittenRelatedness(new MilneWittenOptions(json))
      case "Jaccard" => new JaccardRelatedness(new JaccardOptions(json))

      case "w2v" => new NeuralRelatedness(new NeuralOptions(json))

      //case "LocalClustering" => new LocalClusteringRelatedness(new LocalClusteringOptions(json))

      /*case "LLP" => new LLPRelatedness(json)
      case "MultiLLP" => new MultiLLPRelatedness(json)
      */

      //case "LMModel" => new LMRelatedness(new LMOptions(json))

      //case "CoSimRank" => new CoSimRankRelatedness(new CoSimRankOptions(json))
      // case "PPRCos" => new PPRCosRelatedness(new PPRCosOptions(json))

      case "SVD" => new GraphSVDRelatedness(new GraphSVDOptions(json))
      case "LDA" => new LDARelatedness(new LDAOptions(json))

      //case "ESA" => new ESARelatedness(new ESAOptions(json))

      case _ => throw new IllegalArgumentException("The specified relatedness does not exist %s.".format(relatednessName))
    }
  }


  def getRelatednessName(json: Option[Any]) : String = {
    json match {
      case Some(options: Map[String, Any] @unchecked) =>
        try {
          options("relatedness").toString
        } catch {
          case e: Exception => throw new IllegalArgumentException("Error in getting relatedness name")
        }
      case _ => throw new IllegalArgumentException("Error while matchin json string.")
    }
  }
}