package it.unipi.di.acubelab.wikipediarelatedness.runners

import it.unipi.di.acubelab.wikipediarelatedness.benchmark.{ApproxRelatednessBenchmark, RelatednessBenchmark}
import it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.wikipediarelatedness.dataset.wire.WiReDataset
import it.unipi.di.acubelab.wikipediarelatedness.options._
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.esa.ESARelatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.neural.Word2VecRelatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank.{CoSimRankRelatedness, PPRCosRelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.set.{JaccardRelatedness, LocalClusteringRelatedness, MilneWittenRelatedness}
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer



class RunBenchmark {
  val logger = LoggerFactory.getLogger(classOf[RunBenchmark])

  val wikisim = new WikiSimDataset(Configuration.dataset("procWikiSim"))
  val wire = new WiReDataset(Configuration.wirePiPz("ss"))

  def run() = {

    val ranks = ListBuffer.empty[Tuple2[List[Float], String]]  // [ ( [Pearson, Spearman, Harmonic], RelName ) ]

    for (relatedness <- methods()) {

      try {
        logger.info("%s Benchmark".format(relatedness.toString()))
        logger.info("Standard Relatedness Benchmarking...")

        val bench = new RelatednessBenchmark(wire, relatedness)
        bench.runBenchmark()
        ranks += Tuple2(bench.getPerformance(), relatedness.toString())
      } catch {
        case e : Exception => logger.error("Error while computing %s relatedness.".format(relatedness.toString()))
      }

      //logger.info("Approximated Relatedness Benchmarking...")
      //new ApproxRelatednessBenchmark(wire, relatedness).runBenchmark()
    }

    val sortedRanks = ranks.sortBy(corrsName => corrsName._1(2))
                          .map(corrsName => "[H: %1.2f, P: %1.2f, S: %1.2f] %s"
                              .format(corrsName._1(2), corrsName._1(0), corrsName._1(1),
                                corrsName._2.toString)).reverse

    logger.info("-------------------------")
    logger.info("Final Relatedness Ranking")
    logger.info("\n" + (sortedRanks mkString "\n"))
  }


  def methods() = {

    val relatednessMethods = ListBuffer(
      new MilneWittenRelatedness( new MilneWittenOptions(Some(Map("graph" -> "inGraph"))) ),

      new JaccardRelatedness( new JaccardOptions(Some(Map("graph" -> "inGraph"))) ),
      new JaccardRelatedness( new JaccardOptions(Some(Map("graph" -> "outGraph"))) ),
      new JaccardRelatedness( new JaccardOptions(Some(Map("graph" -> "symGraph"))) ),

      //new Word2VecRelatedness( new Word2VecOptions(Some(Map("model" -> "corpus"))) ),
      //new Word2VecRelatedness( new Word2VecOptions(Some(Map("model" -> "deepWalk"))) )
      new Word2VecRelatedness( new Word2VecOptions(Some(Map("model" -> "sg"))) ),
      new Word2VecRelatedness( new Word2VecOptions(Some(Map("model" -> "dwsg"))) )
    )

    relatednessMethods
    /*// ESA
    for {
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
    }


    relatednessMethods*/
  }

}
