package it.unipi.di.acubelab.graphrel

import it.unipi.di.acubelab.graphrel.benchmark.Benchmark
import it.unipi.di.acubelab.graphrel.dataset.wikisim.{WikiSimDataset, WikiSimProcessing}
import it.unipi.di.acubelab.graphrel.utils.Configuration
import it.unipi.di.acubelab.graphrel.wikipedia.relatedness.RelatednessFactory
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
    val llpOptions = JSON.parseFull(args(0))

    val bvGraphProcessing = new WebGraphProcessor

    // Creates and stores LLP clusters from the raw Wikipedia graph.
    bvGraphProcessing.processLLP(llpOptions)
  }
}

/**
  * Redirects Wikipedia titles.
  */
object WikiSimProcess {
  def main(args: Array[String]) {
    val wikiSimDataset = new WikiSimDataset(Configuration.dataset("wikiSim"))
    val processor = new WikiSimProcessing(wikiSimDataset)
    processor.process()
  }
}

object Bench {
  def main(args: Array[String]) {
    val relatednessOptions = JSON.parseFull(args(0))
    val relatdness = RelatednessFactory.make(relatednessOptions)

    val dataset = new WikiSimDataset(Configuration.dataset("procWikiSim"))

    val benchmark = new Benchmark(dataset, relatdness)
    benchmark.runBenchmark()
  }
}
