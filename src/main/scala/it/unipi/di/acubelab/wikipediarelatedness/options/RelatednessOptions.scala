package it.unipi.di.acubelab.wikipediarelatedness.options

import it.unipi.di.acubelab.wikipediarelatedness.options.Set.SetOptions


/**
  * Container of the configuration parameters of the available Relatedness algorithms.
  *
  * @param name         Name of the relatedness algorithm
  * @param graph        Underlying Wikipedia Graph
  * @param subGraph     Technique used to generate a subgraph between two nodes
  * @param weights      Relatedness algorithm used to weight the subgraph
  * @param threshold    General threshold
  * @param iterations   PageRank iterations
  * @param pprDecay     PageRank decay
  * @param csrDecay     CoSimRank decay
  * @param model        Embedding model
  */
case class RelatednessOptions(
  name: String = null,

  graph: String = "in",
  subGraph: String = "esa",
  weights: String = "milnewitten",

  threshold: Int = 100,

  iterations: Int = 30,
  pprDecay: Float = 0.8f,
  csrDecay: Float = 0.8f,

  model: String = "corpus"
)



/**
  * Factory class for building RelatednessOptions from console arguments.
  *
  */
object RelatednessOptions {

  def make(args: Array[String]) {
    val parser = new scopt.OptionParser[RelatednessOptions]("relatednessoptions") {

      opt[String]("name").action((x, conf) => conf.copy(name = x)).text("Name of the relatedness algorithm")

      //
      // Graph Parameters
      opt[String]("graph").action((x, conf) => conf.copy(graph = x)).text("Underlying Wikipedia Graph")

      opt[String]("subGraph").action((x, conf) =>
        conf.copy(subGraph = x)).text("Technique used to generate a subgraph between two nodes")

      opt[String]("weights").action((x, conf) =>
        conf.copy(weights = x)).text("Relatedness algorithm used to weight the subgraph")

      //
      // Threshold
      opt[Int]("threshold").action((x, conf) => conf.copy(threshold = x)).text("PageRank iterations")

      //
      // Random Walk Parameters
      opt[Int]("iterations").action((x, conf) => conf.copy(iterations = x)).text("PageRank iterations")

      opt[Float]("pprDecay").action((x, conf) => conf.copy(pprDecay = x)).text("PageRank decay")

      opt[Float]("csrDecay").action((x, conf) => conf.copy(csrDecay = x)).text("CoSimRank decay")

      //
      // Embeddings
      opt[String]("model").action((x, conf) => conf.copy(model = x)).text("Embedding model")

    }

    parser.parse(args, SetOptions()) match {
      case Some(config) => config
      case None => throw new IllegalArgumentException("RelatednessOptions: Error while parsing %s"
        .format(args.toString))
    }
  }
}