package it.unipi.di.acubelab.wikipediarelatedness

import it.unipi.di.acubelab.wikipediarelatedness.benchmark.Benchmark
import it.unipi.di.acubelab.wikipediarelatedness.dataset.DatasetFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{RelatednessFactory, RelatednessOptions}
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