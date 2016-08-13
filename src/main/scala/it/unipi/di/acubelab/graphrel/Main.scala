package it.unipi.di.acubelab.graphrel

import it.unipi.di.acubelab.graphrel.analysis.WikiSimAnalysis
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
    val llpOptions =  if (args.length > 0) JSON.parseFull(args(0)) else Some(Map())

    val bvGraphProcessing = new WebGraphProcessor

    // Creates and stores LLP clusters from the raw Wikipedia graph.
    bvGraphProcessing.processLLP(llpOptions)
  }
}

object MultiLLP {
  def main(args: Array[String]) {
    val llpOptions =  if (args.length > 0) JSON.parseFull(args(0)) else Some(Map())

    val bvGraphProcessing = new WebGraphProcessor

    bvGraphProcessing.processMultiLLP(llpOptions)
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

object GridLLP {
  def main(args: Array[String]) = {
    for {
      nLabels <- 1 to 10 by 1
      maxUpdates <- 100 to 1000 by 100
    } {
      val llpJson = """{"nLabels": %d, "maxUpdates": %d}""".format(nLabels, maxUpdates)
      LLP.main(Array(llpJson))

      val llpBench = """{"relatedness": "LLP", "nLabels": %d, "maxUpdates": %d}""".format(nLabels, maxUpdates)
      Bench.main(Array(llpBench))
    }
  }
}


object GridCoSimRank {
  def main(args: Array[String]) = {
    val weightings = Array(
      """{"relatedness": "MilneWitten"}""",
      """{}""",

      """{"relatedness": "Jaccard", "graph": "inGraph"}""",
      """{"relatedness": "Jaccard", "graph": "outGraph"}""",

      """{"relatedness": "w2v", "graph": "corpus"}""",
      """{"relatedness": "w2v", "graph": "deepWalk"}""",

      """{"relatedness": "MultiLLP"}"""
    )
    for {
      weighting <- weightings
      algo <- Array("CoSimRank", "PPRCos")
      graph <- Array("inGraph,outGraph", "inGraph", "outGraph")
      iters <- Array(100, 80, 50, 30, 10, 5, 3)
      decay <- Array(0.8)  //0.4 until 1.0 by 0.2
    } {
      var csrJson = ""

      if(weighting != "{}")
        csrJson = """{"relatedness": "%s", "iters": %d, "decay": %1.3f, "graph": "%s", "weighting": %s}"""
          .format(algo, iters, decay, graph, weighting)
      else
        csrJson = """{"relatedness": "%s", "iters": %d, "decay": %1.3f}""".format(algo, iters, decay)

      Bench.main(Array(csrJson))
    }
  }
}


object AllBench {
  val jsons = Array(
    """{"relatedness": "MilneWitten"}""",

    """{"relatedness": "Jaccard", graph: "inGraph"}""",
    """{"relatedness": "Jaccard", graph: "outGraph"}""",
    """{"relatedness": "Jaccard", graph: "symGraph"}""",

    """{"relatedness": "w2v", graph: "corpus"}""",
    """{"relatedness": "w2v", graph: "deepWalk"}""",
    """{"relatedness": "w2v", graph: "deepCorpus"}""",

    """{"relatedness": "LocalClustering"}""",

    """{"relatedness": "MultiLLP"}""",

    """{"relatedness": "CoSimRank", "iters": 5, "decay": 0.8}"""

  )

  jsons.foreach(json => Bench.main(Array(json)))
}

object Analysis {
  def main(args: Array[String]): Unit = {
    val options =  if (args.length > 0) JSON.parseFull(args(0)) else Some(Map())
    options match {
      case Some(opts: Map[String, Any] @unchecked) =>
        val wikiAnalysis = new WikiSimAnalysis(opts)
        wikiAnalysis.computeAnalysis()
      case _ => throw new IllegalArgumentException("Options Analysis do not match with nothing!")
    }
  }
}