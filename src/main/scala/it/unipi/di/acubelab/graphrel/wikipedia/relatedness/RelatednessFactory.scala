package it.unipi.di.acubelab.graphrel.wikipedia.relatedness

import it.unipi.di.acubelab.graphrel.wikipedia.WikiGraph


object RelatednessFactory {
  /**
    * @param relateOptions JSON file:
    *                      {
    *                          // Relatedness measure
    *                         "relatedness": MilneWitten/Jaccard/LLP/MultiLLP/w2v
    *
    *                         // Wikipedia graph to be used
    *                         "graph": "in", "out", "sym", "noloopsym"
    *                      }
    *
    * @return Relatedness object, instatiated with the specified parameters.
    */
  def make(relateOptions: Option[Any]) : Relatedness = relateOptions match {

    case Some(opts: Map[String, Any] @unchecked) =>

      val relatednessName = opts("relatedness")
      relatednessName match {

        case "MilneWitten" => new MilneWittenRelatedness(opts)
        case "Jaccard" => new JaccardRelatedness(opts)
        case "w2v" => new EmbeddingRelatedness(opts)
        case "LocalClustering" => new LocalClusteringRelatedness(opts)
        case "LLP" => new LLPRelatedness(opts)
        case "MultiLLP" => new MultiLLPRelatedness(opts)
        case "LMModel" => new LMRelatedness(opts)

        case _ => throw new IllegalArgumentException("The specified relatedness does not exist %s."
          .format(relatednessName))
      }

    case _ => throw new IllegalArgumentException("Relatedness Options are not valid: %s".format(relateOptions))
  }
}
