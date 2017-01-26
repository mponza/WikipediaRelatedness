package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.options.Set.SetOptions
import it.unipi.di.acubelab.wikipediarelatedness.options._
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.esa.{ESARelatedness, IBMESARelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.latent.{GraphSVDRelatedness, LDARelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.neural.{LINERelatedness, NeuralRelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank.subgraph.{JungCoSimRankRelatedness, SubCoSimRankRelatedness, SubPPRCosRelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank.{CoSimRankRelatedness, PPRCosRelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.set.{JaccardRelatedness, JaccardTopRelatedness, LocalClusteringRelatedness, MilneWittenRelatedness}


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

  def make(name: String, options: RelatednessOptions) : Relatedness =  (name, options) match {

    case ("mw" | "MilneWitten", setOpts: SetOptions) => new MilneWittenRelatedness(setOpts)
    case ("jacc" | "Jaccard", setOpts: SetOptions) => new JaccardRelatedness(setOpts)
  }

  def make(json: Option[Any]) : Relatedness = {
    val relatednessName = getRelatednessName(json)

    relatednessName match {
      // Set
      //case "JaccardTop" => new JaccardTopRelatedness(new JaccardTopOptions(json))
      // case "LocalClustering" => new LocalClusteringRelatedness(new LocalClusteringOptions(json))

      // Embeddings
      case "w2v" => new NeuralRelatedness(new Word2VecOptions(json))
      case "LINE" => new LINERelatedness(new LINEOptions(json))

      // ESA

      //

      /*case "LLP" => new LLPRelatedness(json)
      case "MultiLLP" => new MultiLLPRelatedness(json)
      */

      //case "LMModel" => new LMRelatedness(new LMOptions(json))

      // PageRank
      case "CoSimRank" => new CoSimRankRelatedness(new CoSimRankOptions(json))

      case "PPRCos" => new PPRCosRelatedness(new PPRCosOptions(json))


      case "SubCoSimRank" => new JungCoSimRankRelatedness(new SubCoSimRankOptions(json))
      //case "SubPPRCos" => new SubPPRCosRelatedness(new SubPPRCosOptions(json))


      // Latent
      case "SVD" => new GraphSVDRelatedness(new GraphSVDOptions(json))
      case "LDA" => new LDARelatedness(new LDAOptions(json))

      case _ => throw new IllegalArgumentException("The specified relatedness does not exist %s.".format(relatednessName))
    }
  }


  protected def getRelatednessName(args: Array[String]) : String = {
    case class NameConfig(name: String = null)

    val parser = new scopt.OptionParser[NameConfig]("") {}


    def parseString(args: Array[String]) = {
      val parser = new scopt.OptionParser[SetOptions]("setoptions") {

        opt[String]('g', "graph").action( (x, conf) =>
          conf.copy(graph = x) ).text("graph is the graph to be used (in, out, sym). Default: in.")

      }

      parser.parse(args, SetOptions()) match {
        case Some(config) => config
        case None => throw new IllegalArgumentException("MilneWittenOptions: Error while parsing %s"
          .format(args.toString))
      }
    }
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


  def make(relatednessName: String)  : Relatedness = {
    val json = Some(Map("relatedness" -> relatednessName))
    this.make(json)
  }

}