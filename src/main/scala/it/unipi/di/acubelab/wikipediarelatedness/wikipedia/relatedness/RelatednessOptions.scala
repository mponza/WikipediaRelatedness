package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness


/**
  * Container of the configuration parameters of the available Relatedness algorithms.
  *
  * @param name         Name of the relatedness algorithm
  * @param graph        Underlying Wikipedia Graph
  * @param subNodes     Technique used to generate a subgraph between two nodes
  * @param weighter      Relatedness algorithm used to weight the subgraph
  * @param threshold    General threshold
  * @param iterations   PageRank iterations
  * @param pprDecay     PageRank decay
  * @param csrDecay     CoSimRank decay
  * @param model        Embedding model
  */
case class RelatednessOptions(

             //
             // Main Relatedness Options
             name: String = null,

             graph: String = "",

             threshold: Int = 0,

             model: String = "",


             //
             // SubGraph Weighting Options. Weights are compute by using another relatedness method.
             // Used when method is subgraph.

             subNodes: String = "",    // Method used to generate the subgraph nodes.\
             subSize: Int = 50,        // Size of the subgraph (number of nodes)

             // Weigher (relatedness method) and its parameters
             weighter: String = "",

             weighterGraph: String = "",
             weighterModel: String = "",
             weighterThreshold: Int = 0,

             simRanker: String = "",
             iterations: Int = 30,
             pprDecay: Float = 0.1f,
             csrDecay: Float = 0.8f
)



/**
  * Factory for building RelatednessOptions from console arguments.
  *
  */
object RelatednessOptions {

  /**
    * Makes a relatedness options from command line arguments.
    *
    * @param args
    * @return
    */
  def make(args: Array[String]) : RelatednessOptions = {
    val parser = new scopt.OptionParser[RelatednessOptions]("relatednessoptions") {

      opt[String]("name").action((x, conf) => conf.copy(name = x)).text("Name of the relatedness algorithm")

      //
      // Graph Parameters
      opt[String]("graph").action((x, conf) => conf.copy(graph = x)).text("Underlying Wikipedia Graph")

      //
      // Threshold
      opt[Int]("threshold").action((x, conf) => conf.copy(threshold = x)).text("PageRank iterations")

      //
      // Random Walk Parameters
      opt[Int]("iterations").action((x, conf) => conf.copy(iterations = x)).text("PageRank iterations")

      opt[Double]("pprDecay").action((x, conf) => conf.copy(pprDecay = x.toFloat)).text("PageRank decay")

      opt[Double]("csrDecay").action((x, conf) => conf.copy(csrDecay = x.toFloat)).text("CoSimRank decay")

      //
      // Embeddings
      opt[String]("model").action((x, conf) => conf.copy(model = x)).text("Embedding model")


      //
      // SubGraph Method and its parameters
      opt[String]("subNodes").action((x, conf) =>
        conf.copy(subNodes = x)).text("Technique used to generate the node of the subgraph between two nodes")

      opt[Int]("subSize").action((x, conf) =>
        conf.copy(subSize = x)).text("Size of the subgraph (number of nodes).")

      // Parameters used by weighter relatedness to weight the subGraph
      opt[String]("weighter").action((x, conf) =>
        conf.copy(weighter= x)).text("Relatedness algorithm used to weight the subgraph")

      opt[String]("weighterGraph").action((x, conf) =>
        conf.copy(weighterGraph = x)).text("Graph used by weightName Relatedness algorithm.")

      opt[String]("weighterModel").action((x, conf) =>
        conf.copy(weighterModel = x)).text("Model used by weightName Relatedness algorithm.")

      opt[Int]("weighterThreshold").action((x, conf) =>
        conf.copy(weighterThreshold = x)).text("Threshold used by weightName Relatedness algorithm.")

    }

    parser.parse(args, RelatednessOptions()) match {
      case Some(config) => config
      case None => throw new IllegalArgumentException("RelatednessOptions: Error while parsing %s"
        .format(args.toString))
    }
  }

}