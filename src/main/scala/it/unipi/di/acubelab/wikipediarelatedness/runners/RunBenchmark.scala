package it.unipi.di.acubelab.wikipediarelatedness.runners

import it.unipi.di.acubelab.wikipediarelatedness.benchmark.{ApproxRelatednessBenchmark, RelatednessBenchmark}
import it.unipi.di.acubelab.wikipediarelatedness.dataset.RelatednessDataset
import it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.wikipediarelatedness.dataset.wire.{WiReDataset, WiReGT}
import it.unipi.di.acubelab.wikipediarelatedness.options._
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.esa.ESARelatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.neural.Word2VecRelatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank.{CoSimRankRelatedness, PPRCosRelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.set.{JaccardRelatedness, LocalClusteringRelatedness, MilneWittenRelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{LMRelatedness, Relatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.latent.{GraphSVDRelatedness, LDARelatedness}
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer



class RunBenchmark {
  val logger = LoggerFactory.getLogger(classOf[RunBenchmark])

  val wikisim = new WikiSimDataset(Configuration.dataset("procWikiSim"))
  val wiReGTList = WiReGT.makeDatasets()

  def run() : Unit  = {
    run(wikisim)

    for(dataset <- wiReGTList) {
      run(dataset)
    }

  }


  def run(dataset: RelatednessDataset) = {
    logger.info("Benchmarking upon %s".format(dataset.toString()))

    val ranks = ListBuffer.empty[Tuple2[List[Float], String]]  // [ ( [Pearson, Spearman, Harmonic], RelName ) ]

    for (relatedness <- methods()) {

      try {
        logger.info("%s Benchmark".format(relatedness.toString()))
        logger.info("Standard Relatedness Benchmarking...")

        val bench = new RelatednessBenchmark(dataset, relatedness)
        bench.runBenchmark()
        ranks += Tuple2(bench.getPerformance(), relatedness.toString())

      } catch {
        case e : Exception => logger.error("Error while computing %s relatedness.".format(relatedness.toString()))
      }
    }

    val sortedRanks = ranks.sortBy(corrsName => corrsName._1(2))
                          .map(corrsName => "[H: %1.2f, P: %1.2f, S: %1.2f] %s"
                              .format(corrsName._1(2), corrsName._1(0), corrsName._1(1),
                                corrsName._2.toString)).reverse

    logger.info("-------------------------")
    logger.info("Final Relatedness Ranking")
    logger.info("\n" + (sortedRanks mkString "\n"))
    logger.info("-------------------------")
  }


  def methods() = {

    val relatednessMethods = ListBuffer(
        new MilneWittenRelatedness( new MilneWittenOptions(Some(Map("graph" -> "inGraph"))) ),

        new JaccardRelatedness( new JaccardOptions(Some(Map("graph" -> "inGraph"))) ),
        new JaccardRelatedness( new JaccardOptions(Some(Map("graph" -> "outGraph"))) ),
        new JaccardRelatedness( new JaccardOptions(Some(Map("graph" -> "symGraph"))) )
      )

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
    /*for {
      threshold <- List(625, 650, 1000, 2000, 3000)
    } {
      relatednessMethods += new ESARelatedness( new ESAOptions(Some(Map("threshold" -> threshold.toDouble))) )
    }

    // PageRank based
    for (decay <- List(0.8, 0.9, 1.0)) {
      val csrOptions = new CoSimRankOptions(Some(Map("iterations" -> 30.toDouble, "pprDecay" -> decay, "csrDecay" -> decay)))
      relatednessMethods += new CoSimRankRelatedness()

      val pprOptions = new PPRCosOptions(Some(Map("iterations" -> 30.toDouble, "pprDecay" -> decay)))
      relatednessMethods += new PPRCosRelatedness(pprOptions)
    }*/


    relatednessMethods
  }

}
