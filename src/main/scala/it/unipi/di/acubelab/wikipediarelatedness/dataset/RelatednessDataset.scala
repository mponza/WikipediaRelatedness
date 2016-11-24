package it.unipi.di.acubelab.wikipediarelatedness.dataset

trait RelatednessDataset extends Traversable[WikiRelateTask] {
  override def toString() : String  // Dataset name
}




/*


 """{"relatedness": "MilneWitten"}""",
    """{}""",

    """{"relatedness": "Jaccard", "graph": "inGraph"}""",
    """{"relatedness": "Jaccard", "graph": "outGraph"}""",

    """{"relatedness": "w2v", "graph": "corpus"}""",
    """{"relatedness": "w2v", "graph": "deepWalk"}""",

    """{"relatedness": "MultiLLP"}"""
def make(json: Option[Any]) : Relatedness = {
  val relatednessName = getRelatednessName(json)

  relatednessName match {
    // Set
    case "MilneWitten" => new MilneWittenRelatedness(new MilneWittenOptions(json))
    case "Jaccard" => new JaccardRelatedness(new JaccardOptions(json))
    case "LC" | "localClustering" => new LocalClusteringRelatedness(new LocalClusteringOptions(json))
    case "JaccardTop" => new JaccardTopRelatedness(new JaccardTopOptions(json))
    // case "LocalClustering" => new LocalClusteringRelatedness(new LocalClusteringOptions(json))

    // Embeddings
    case "w2v" => new Word2VecRelatedness(new Word2VecOptions(json))
    case "LINE" => new LINERelatedness(new LINEOptions(json))

    // ESA
    case "IBMESA" => new IBMESARelatedness(new IBMESAOptions(json))
    case "ESA" => new ESARelatedness(new ESAOptions(json))

    //

    /*case "LLP" => new LLPRelatedness(json)
    case "MultiLLP" => new MultiLLPRelatedness(json)
    */

    //case "LMModel" => new LMRelatedness(new LMOptions(json))

    // PageRank
    case "CoSimRank" => new CoSimRankRelatedness(new CoSimRankOptions(json))
    case "CoSubSimRank" => new CoSubSimRankRelatedness(new CoSubSimRankOptions(json))

    case "PPRCos" => new PPRCosRelatedness(new PPRCosOptions(json))
    case "PPRSubCos" => new PPRSubCosRelatedness(new PPRSubCosOptions(json))


    // Latent
    case "SVD" => new GraphSVDRelatedness(new GraphSVDOptions(json))
    case "LDA" => new LDARelatedness(new LDAOptions(json))

    case _ => throw new IllegalArgumentException("The specified relatedness does not exist %s.".format(relatednessName))
  }
}
*
*
*
* */