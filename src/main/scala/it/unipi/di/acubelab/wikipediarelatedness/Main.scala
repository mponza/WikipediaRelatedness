package it.unipi.di.acubelab.wikipediarelatedness

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{RelatednessFactory, RelatednessOptions}


object Benchmark {
  def main(args: Array[String]) {

    val options = RelatednessOptions.make(args)
    val relatedness = RelatednessFactory.make(options)

    println(relatedness)

  }
}
