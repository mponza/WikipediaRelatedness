package it.unipi.di.acubelab.wikipediarelatedness.runners

/*import it.unipi.di.acubelab.wikipediarelatedness.benchmark.{ApproxRelatednessBenchmark, Benchmark}
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateDataset
import it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.wikipediarelatedness.dataset.wire.{WiReDataset, WiReGT}
import it.unipi.di.acubelab.wikipediarelatedness.options._
import it.unipi.di.acubelab.wikipediarelatedness.utils.OldConfiguration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.esa.ESARelatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.neural.NeuralRelatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank.{CoSimRankRelatedness, PPRCosRelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.set.{JaccardRelatedness, LocalClusteringRelatedness, MilneWittenRelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{LMRelatedness, Relatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.latent.{GraphSVDRelatedness, LDARelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank.subgraph.context.ContextCliqueCoSimRankRelatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank.subgraph.{JungCliqueCoSimRankRelatedness, JungCoSimRankRelatedness, SubCoSimRankRelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.paths.KShortestPathsRelatedness
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

*/
/*
class RunBenchmark {
  val logger = LoggerFactory.getLogger(classOf[RunBenchmark])

  val wikisim = new WikiSimDataset(OldConfiguration.dataset("procWikiSim"))
  val wiReGTList = WiReGT.makeDatasets()

  def run() : Unit  = {
    run(wikisim)

    for(dataset <- wiReGTList) {
      run(dataset)
    }
  }



  def run(dataset: WikiRelateDataset) = {
    logger.info("Benchmarking upon %s".format(dataset.toString()))

    val ranks = ListBuffer.empty[Tuple2[List[Float], String]]  // [ ( [Pearson, Spearman, Harmonic], RelName ) ]

    for (relatedness <- methods()) {

      try {
        logger.info("%s Benchmark".format(relatedness.toString()))
        logger.info("Standard Relatedness Benchmarking...")

        val bench = new Benchmark(dataset, relatedness)
        bench.run()
        ranks += Tuple2(bench.getPerformance(), relatedness.toString())

      } catch {
          case e : Exception => logger.error("Error while computing %s relatedness: %s".format(relatedness.toString(), e.toString))
        }
    }

    val sortedRanks = ranks.sortBy(corrsName => corrsName._1(2))
                          .map(corrsName => "[P: %1.2f, S: %1.2f, H: %1.2f, ] %s"
                              .format(corrsName._1(0), corrsName._1(1), corrsName._1(2),
                                corrsName._2.toString)).reverse

    logger.error("------------ %s -------------".format(dataset.toString()))
    logger.error("Final Relatedness Ranking")
    logger.error("\n" + (sortedRanks mkString "\n"))
    logger.error("-------------------------")

  }


  def methods() = {

    val relatednessMethods = ListBuffer.empty[Relatedness]
    //relatednessMethods +=   new MilneWittenRelatedness( new MilneWittenOptions(Some(Map("graph" -> "inGraph"))) )

        //new JaccardRelatedness( new JaccardOptions(Some(Map("graph" -> "inGraph"))) ),
       // new JaccardRelatedness( new JaccardOptions(Some(Map("graph" -> "outGraph"))) ),
       // new JaccardRelatedness( new JaccardOptions(Some(Map("graph" -> "symGraph"))) )
     //)
/*
    for {
      threshold <- List(10, 50, 100).sorted //, 50, 100, 500)//, 50, 100, 1000, 2000).sorted //, 30, 50, 100, 200, 500, 1000)//, 1000, 2000)
      sub <- List("esa", "dw", "w2v")
      wikiGraphName <- List("outGraph")
      pprDecay <- List(0.1)//, 0.2, 0.3)
      csrDecay <- List(0.9)//, 0.8, 0.7)
      iters <- List(10)//, 30, 80)
    } {
      val subCSROpts = new SubCoSimRankOptions(
        Some(
          Map("weighting" -> "MilneWitten", "subGraph" -> sub, "threshold" -> threshold,
              "wikiGraph" -> wikiGraphName, "pprDecay" -> pprDecay, "csrDecay" -> csrDecay, "iterations" -> iters)
        )
      )
      logger.info("%s".format(subCSROpts))
      relatednessMethods += new JungCliqueCoSimRankRelatedness(subCSROpts)
    }


    for {
      threshold <- List(30, 90, 300).sorted //, 50, 100, 500)//, 50, 100, 1000, 2000).sorted //, 30, 50, 100, 200, 500, 1000)//, 1000, 2000)
      sub <- List("cxt-w2v", "cxt-dw", "pure-cxt-w2v", "pure-cxt-dw") //, "dw", "w2v")
      wikiGraphName <- List("outGraph")
      pprDecay <- List(0.1)//, 0.2, 0.3)
      csrDecay <- List(0.9)//, 0.8, 0.7)
      iters <- List(10) //, 30, 80)
    } {
      val subCSROpts = new ContextSubCoSimRankOptions(
        Some(
          Map("weighting" -> "MilneWitten", "subGraph" -> sub, "threshold" -> threshold,
            "wikiGraph" -> wikiGraphName, "pprDecay" -> pprDecay, "csrDecay" -> csrDecay, "iterations" -> iters)
        )
      )
      logger.info("%s".format(subCSROpts))
      relatednessMethods += new ContextCliqueCoSimRankRelatedness(subCSROpts)
    }
*/
/*
    for {
      threshold <- List(5, 10).sorted ///, 50).sorted //, 50, 100, 500)//, 50, 100, 1000, 2000).sorted //, 30, 50, 100, 200, 500, 1000)//, 1000, 2000)
      sub <- List("esa") //, "dw", "w2v")
      wikiGraphName <- List("outGraph")
      k <- List(3, 5)//, 50).sorted
      pathFun <- List("avg", "max")//, "min", "max", "hmean")
      kFun <- List("avg", "max")//, "min", "max", "hmean")
      combFun <- List("avg", "max")//, "min", "max", "hmean")
    } {
      val kSPOptions = new KShortestPathsOptions(
        Some(
          Map("weighting" -> "MilneWitten", "subGraph" -> sub, "threshold" -> threshold,
              "k" -> k, "pathFun" -> pathFun, "kFun" -> kFun, combFun -> "combFun"
          )
        )
      )

      logger.info("%s".format(kSPOptions))
      relatednessMethods += new KShortestPathsRelatedness(kSPOptions)
    }
*/
    // Language Model
    //relatednessMethods += new LMRelatedness( new LMOptions() )

      // Latent
    //relatednessMethods += new LDARelatedness( new LDAOptions() )
    //relatednessMethods += new GraphSVDRelatedness( new GraphSVDOptions() )

    // Embeddings
    //relatednessMethods += new Word2VecRelatedness( new Word2VecOptions(Some(Map("model" -> "corpus"))) ),
    //relatednessMethods += new Word2VecRelatedness( new Word2VecOptions(Some(Map("model" -> "deepWalk"))) )
    //relatednessMethods += new Word2VecRelatedness( new Word2VecOptions(Some(Map("model" -> "sg"))) )
    //relatednessMethods += new Word2VecRelatedness( new Word2VecOptions(Some(Map("model" -> "dwsg"))) )

    // ESA
    //for {
    //  threshold <- List(625, 650, 1000, 2000, 3000)
    //} {
    //  relatednessMethods += new ESARelatedness( new ESAOptions(Some(Map("threshold" -> threshold.toDouble))) )
    //}

    // PageRank based
    /*for (decay <- List(0.8, 0.9, 1.0)) {
      val csrOptions = new CoSimRankOptions(Some(Map("iterations" -> 30.toDouble, "pprDecay" -> decay, "csrDecay" -> decay)))
      relatednessMethods += new CoSimRankRelatedness()

      val pprOptions = new PPRCosOptions(Some(Map("iterations" -> 30.toDouble, "pprDecay" -> decay)))
      relatednessMethods += new PPRCosRelatedness(pprOptions)
    }*/


    relatednessMethods
  }

}
*/