package it.unipi.di.acubelab.wikipediarelatedness.utils

import org.rogach.scallop.ScallopConf

class TwoStageArgs(arguments: Seq[String]) extends ScallopConf(arguments) {
  // .bin graph filenames
  val outgraph = opt[String](required = true)
  val ingraph = opt[String](required = true)

  // .bin caches
  val cachetopnodes = opt[String](required = true)
  val cacheweights = opt[String](required = true)

  // Two-Stage's k
  val k = opt[Int](required = true)

  // input data of which compute pairs
  val querypairs = opt[String](required = true)
  // where save output relatedness scores
  val output = opt[String](required = true)

  verify()
}