package it.unipi.di.acubelab.wikipediarelatedness

import it.unipi.di.acubelab.wikipediarelatedness.benchmark.Benchmark
import it.unipi.di.acubelab.wikipediarelatedness.dataset.DatasetFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{RelatednessFactory, RelatednessOptions}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.neighbors.MilneWittenTopK
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.{TopKCacher, TopKFactory}
import org.slf4j.LoggerFactory


/**
  * Experiments a relatedness algorithm over all available datasets.
  *
  */
object Main {
  val logger = LoggerFactory.getLogger("Main")

  def main(args: Array[String]) {

    val options = RelatednessOptions.make(args)
    val relatedness = RelatednessFactory.make(options)

    DatasetFactory.datasets().foreach {
      dataset =>

        val benchmark = new Benchmark(dataset, relatedness)
        benchmark.run()
    }
  }

}

object MilneWittenCacher {
  def main(args: Array[String]) {
    val topk = new MilneWittenTopK("in")
    val dataset = DatasetFactory.datasets()

    TopKCacher.generate(topk, dataset.flatten.slice(0, 5), topk.getCachePath)
  }
}
/*
object Jung {
  def main(args: Array[String]) {
    val wjg = new WikiJungBVGraph("out")
    val ppr = new PPRRanker(5, 0.1)

    println( ppr.similarity(17547, 27546, wjg) )
  }
}*/

/*
object CSR {

  def main(args: Array[String]) {

    CoSimRankCache.generateCache( DatasetFactory.datasets().flatten.toList, 5, 0.8f )
    CoSimRankCache.generateCache( DatasetFactory.datasets().flatten.toList, 10, 0.8f )

    CoSimRankCache.generateCache( DatasetFactory.datasets().flatten.toList, 5, 0.9f )
    CoSimRankCache.generateCache( DatasetFactory.datasets().flatten.toList, 10, 0.9f )
  }
}*/