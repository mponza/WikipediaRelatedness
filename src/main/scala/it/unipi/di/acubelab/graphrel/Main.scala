package it.unipi.di.acubelab.graphrel

import it.unipi.di.acubelab.graphrel.wikipedia.relatedness.RelatednessFactory
import it.unipi.di.acubelab.graphrel.wikipedia.processing.statistics.WikiStats
import it.unipi.di.acubelab.graphrel.wikipedia.processing.webgraph.WebGraphProcessor

import scala.util.parsing.json.JSON

object BVGraph {
  def main(args: Array[String]) {
    val bvGraphProcessing = new WebGraphProcessor

    // Creates and stores BVGraph from the raw Wikipedia graph.
    bvGraphProcessing.generateBVGraph
  }
}

object LLP {
  def main(args: Array[String]) {
    val bvGraphProcessing = new WebGraphProcessor

    // Creates and stores BVGraph from the raw Wikipedia graph.
    bvGraphProcessing.processLLP
  }
}

object Stats {
  def main(args: Array[String]) {
    val wikiStats = new WikiStats

    wikiStats.processStatistics
  }
}

object Relate {
  def main(args: Array[String]) {
    val relateOptions = JSON.parseFull(args(0))
    println(relateOptions)
    val relatdness = RelatednessFactory.make(relateOptions)
  }
}