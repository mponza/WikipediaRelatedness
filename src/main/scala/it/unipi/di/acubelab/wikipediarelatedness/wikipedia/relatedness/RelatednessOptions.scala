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
  * @param pprAlpha     PageRank decay
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
                               // Linear combination of two relatedness (name == comb)
                               firstname: String = "",
                               firstgraph: String = "",
                               firstmodel: String = "",

                               secondname: String = "",
                               secondgraph: String = "",
                               secondmodel: String = "",
                               secondthreshold: Int = 0,

                               lambda: Double = 0.5,  // lambda * firstname + (1 - lambda) * secondname


                               //
                               // SubGraph Weighting Options. Weights are compute by using another relatedness method.
                               // Used when method is subgraph.

                               subNodes: String = "", // Method used to generate the subgraph nodes.\
                               subSize: Int = 50, // Size of the subgraph (number of nodes)

                               // Weigher (relatedness method) and its parameters
                               weighter: String = "",

                               weighterGraph: String = "",
                               weighterModel: String = "",
                               weighterThreshold: Int = 0,

                               simRanker: String = "",
                               iterations: Int = 30,
                               pprAlpha: Float = 0.1f,
                               csrDecay: Float = 0.8f,


                               //
                               // Neural Embeddings parameters
                               size: Int = 100,
                               order: Int = 2,
                               negative: Int = 5,
                               sample: Int = 5,
                               rho: Float = 0.025f



) {

  def getWeighterRelatednessOptions = new RelatednessOptions(
                                                name = this.weighter,
                                                graph = this.weighterGraph,
                                                model = this.weighterModel,
                                                threshold = this.weighterThreshold,

                                                firstname = this.firstname,
                                                firstgraph = this.firstgraph,
                                                firstmodel = this.firstmodel,

                                                secondname = this.secondname,
                                                secondgraph = this.secondgraph,
                                                secondmodel = this.secondmodel,


                                                lambda = this.lambda
                                              )


  /**
    * Returns RelatednessOptions configurated for firstname relatedness.
    * @return
    */
  def getFirstRelatednessOptions = new RelatednessOptions(
                                                 name = this.firstname,
                                                 graph = this.firstgraph,
                                                 model = this.firstmodel
                                              )

  /**
    * Returns RelatednessOptions configurated for secondname relatedness.
    *
    * @return
    */
  def getSecondRelatednessOptions = new RelatednessOptions(
                                                name = this.secondname,
                                                graph = this.secondgraph,
                                                model = this.secondmodel,
                                                threshold = this.secondthreshold
                                              )

}



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
      // Embeddings
      opt[String]("model").action((x, conf) => conf.copy(model = x)).text("Embedding model")


      //
      // Mixed relatedness
      opt[String]("firstname").action((x, conf) => conf.copy(firstname = x)).text("First relatedness")
      opt[String]("firstgraph").action((x, conf) => conf.copy(firstgraph = x)).text("First relatedness graph")
      opt[String]("firstmodel").action((x, conf) => conf.copy(firstmodel = x)).text("First relatedness model")

      opt[String]("secondname").action((x, conf) => conf.copy(secondname = x)).text("Second relatedness")
      opt[String]("secondgraph").action((x, conf) => conf.copy(secondgraph = x)).text("Second relatedness graph")
      opt[String]("secondmodel").action((x, conf) => conf.copy(secondmodel = x)).text("Second relatedness model")
      opt[Int]("secondthreshold").action((x, conf) => conf.copy(secondthreshold = x)).text("Second relatedness threshold")




      opt[Double]("lambda").action((x, conf) => conf.copy(lambda = x)).text("Linear combination weight of the first relatedness")


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


      //
      // Random Walk Parameters
      opt[String]("simRanker").action((x, conf) => conf.copy(simRanker = x)).text("SimRank method.")

      opt[Int]("iterations").action((x, conf) => conf.copy(iterations = x)).text("PageRank iterations")

      opt[Double]("pprAlpha").action((x, conf) => conf.copy(pprAlpha = x.toFloat)).text("PageRank decay")

      opt[Double]("csrDecay").action((x, conf) => conf.copy(csrDecay = x.toFloat)).text("CoSimRank decay")


      //
      // Neural Embeddings parameters
      opt[Int]("size").action((x, conf) => conf.copy(size = x)).text("Embeddings Size")
      opt[Int]("order").action((x, conf) => conf.copy(order = x)).text("Embeddings Order")
      opt[Int]("negative").action((x, conf) => conf.copy(negative = x)).text("Embeddings Negative")
      opt[Int]("sample").action((x, conf) => conf.copy(sample = x)).text("Embeddings Sample")
      opt[Double]("rho").action((x, conf) => conf.copy(rho = x.toFloat)).text("Embeddings Rho")

    }

    parser.parse(args, RelatednessOptions()) match {
      case Some(config) => config
      case None => throw new IllegalArgumentException("RelatednessOptions: Error while parsing %s"
        .format(args.toString))
    }
  }

}