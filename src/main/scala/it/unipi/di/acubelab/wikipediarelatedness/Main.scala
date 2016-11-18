package it.unipi.di.acubelab.wikipediarelatedness

import java.io.{File, PrintWriter}
import java.nio.file.Paths

import it.unimi.dsi.webgraph.algo.StronglyConnectedComponents
import it.unipi.di.acubelab.wikipediarelatedness.analysis.{AllDistanceAnalyzer, DistanceAnalyzer, NYTMerger, WikiEntityType}
import it.unipi.di.acubelab.wikipediarelatedness.benchmark.{ClassificationBenchmark, RelatednessBenchmark}
import it.unipi.di.acubelab.wikipediarelatedness.dataset.nyt.NYTDataset
import it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.Classification
import it.unipi.di.acubelab.wikipediarelatedness.serialization.WikiMTX
import it.unipi.di.acubelab.wikipediarelatedness.utils.CoreNLP
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping.WikiTypeMapping
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa.LuceneProcessing
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa.ESACache
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa.lemma.{LemmaLuceneIndex, LemmaLuceneProcessing}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.triangles.LocalClusteringProcessing
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.{WebGraphProcessor, WikiGraphFactory}

import scala.collection.mutable.ListBuffer

//import it.unipi.di.acubelab.wikipediarelatedness.analysis.WikiSimAnalysis
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessFactory

import scala.util.parsing.json.JSON

/*
object BVGraph {
  def main(args: Array[String]) {
    val bvGraphProcessing = new WebGraphProcessor

    // Creates and stores BVGraph from the raw Wikipedia graph.
    bvGraphProcessing.generateBVGraph
  }
}

object Distances {
  def main(args: Array[String]): Unit = {
    val wikiSimDataset = new WikiSimDataset(Configuration.dataset("procWikiSim"))

    val bvGraphProcessing = new WebGraphProcessor
    bvGraphProcessing.computeDistances(wikiSimDataset)
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
*/

object Core {
  def main(args: Array[String]): Unit = {
    val s = "there was a time when ent_12345 was so cool and he was awesome but the house was dark."

  }
}


object Bench {
  def main(args: Array[String]) {
    val relatednessOptions = JSON.parseFull(args(0))
    val relatdness = RelatednessFactory.make(relatednessOptions)

    val dataset = new WikiSimDataset(Configuration.dataset("procWikiSim"))

    val benchmark = new RelatednessBenchmark(dataset, relatdness)
    benchmark.runBenchmark()
  }
}



object ClassBench {
  def main(args: Array[String]) {
    val relatednessOptions = JSON.parseFull(args(0))
    val relatdness = RelatednessFactory.make(relatednessOptions)

    val dataset = new WikiSimDataset(Configuration.dataset("procWikiSim"))

    val benchmark = new ClassificationBenchmark(dataset, relatdness)
    benchmark.runBenchmark()
  }
}




object Text {
  def main(args: Array[String]): Unit = {
    //println(new LemmaLuceneIndex().wikipediaBody(47197315))
    new LemmaLuceneIndex().vectorSpaceProjection(47197315)
  }
}


object BenchCoSimRank {
  def main(args: Array[String]) {
    for {
      iterations <- List(5, 10)
      pprDecay <- List(0.5f, 0.8f)//0.2f to 0.8f by 0.2f
      csrDecay <- List(0.5f, 0.8f)//0.2f to 0.8f by 0.2f
    } {

      try {
        val s =
          """{"relatedness": "CoSimRank", "iterations": %d, "pprDecay": %1.2f, "csrDecay": %1.2f}"""
            .formatLocal(java.util.Locale.US, iterations, pprDecay, csrDecay)

        val relatednessOptions = JSON.parseFull(s)
        val relatdness = RelatednessFactory.make(relatednessOptions)

        val dataset = new WikiSimDataset(Configuration.dataset("procWikiSim"))

        val benchmark = new RelatednessBenchmark(dataset, relatdness)
        benchmark.runBenchmark()
      } catch {
        case e: Exception => println(e)

      }
    }
  }
}


object BenchPPRCos {
  def main(args: Array[String]) {
    for {
      iterations <- List(5, 10, 20)
      pprDecay <- List(0.2f, 0.5f, 0.8f)
    } {

      try {
        val s =
          """{"relatedness": "PPRCos", "iterations": %d, "pprDecay": %1.2f}"""
            .formatLocal(java.util.Locale.US, iterations, pprDecay)

        val relatednessOptions = JSON.parseFull(s)
        val relatdness = RelatednessFactory.make(relatednessOptions)

        val dataset = new WikiSimDataset(Configuration.dataset("procWikiSim"))

        val benchmark = new RelatednessBenchmark(dataset, relatdness)
        benchmark.runBenchmark()
      } catch {
        case e: Exception => println(e)

      }
    }
  }
}



object BenchIBMESA {
  def main(args: Array[String]) = {
    for {
      threshold <- List(500, 650, 1000, 1500, 2000, 3000, 5000, 10000, 15000)
    } {
      val s = """{"relatedness": "IBMESA", "threshold": %d}""".format(threshold)

      val relatednessOptions = JSON.parseFull(s)
      val relatdness = RelatednessFactory.make(relatednessOptions)

      val dataset = new WikiSimDataset(Configuration.dataset("procWikiSim"))

      val benchmark = new RelatednessBenchmark(dataset, relatdness)
      benchmark.runBenchmark()
    }
  }
}

object ESABench {
  def main(args: Array[String]) = {
    val performance = ListBuffer.empty[Tuple2[Int, List[Float]]]
    for {
      threshold <- (50 to 10000 by 50).toList
    } {
      val s = """{"relatedness": "ESA", "threshold": %d}""".format(threshold)

      val relatednessOptions = JSON.parseFull(s)
      val relatdness = RelatednessFactory.make(relatednessOptions)

      val dataset = new WikiSimDataset(Configuration.dataset("procWikiSim"))

      val benchmark = new RelatednessBenchmark(dataset, relatdness)
      benchmark.runBenchmark()

      performance += Tuple2(threshold, benchmark.getPerformance())
    }

    println(performance.sortBy(p => p._2(2)))
  }
}




object JaccardTopBench {
  def main(args: Array[String]) = {
    val performance = ListBuffer.empty[Tuple2[Int, List[Float]]]
    for {
      threshold <- (10 to 10000 by 50).toList
    } {
      val s = """{"relatedness": "JaccardTop", "threshold": %d}""".format(threshold)

      val relatednessOptions = JSON.parseFull(s)
      val relatdness = RelatednessFactory.make(relatednessOptions)

      val dataset = new WikiSimDataset(Configuration.dataset("procWikiSim"))

      val benchmark = new RelatednessBenchmark(dataset, relatdness)
      benchmark.runBenchmark()

      performance += Tuple2(threshold, benchmark.getPerformance())
    }

    println(performance.sortBy(p => p._2(2)).reverse)
  }
}


object LINEBench {
  def main(args: Array[String]) {
    for {
      size <- List(100, 200, 500)
      order <- List(1)
      negative <- List(5, 8, 10)
    } {

      try {
        val s =
          """{"relatedness": "LINE", "size": %d, "order": %d, "negative": %d}""".format(size, order, negative)

        val relatednessOptions = JSON.parseFull(s)
        val relatdness = RelatednessFactory.make(relatednessOptions)

        val dataset = new WikiSimDataset(Configuration.dataset("procWikiSim"))

        val benchmark = new RelatednessBenchmark(dataset, relatdness)
        benchmark.runBenchmark()
      } catch {
        case e: Exception => println(e)

      }
    }
  }
}


object ClustProcessing {
  def main(args: Array[String]) = {
    val llcp = new LocalClusteringProcessing
    llcp.generateClusteringCoefficients()
  }
}


object ESACacher {
  def main(args: Array[String]) = {
    val esaCache = new ESACache()
    val dataset = new WikiSimDataset(Configuration.dataset("procWikiSim"))

    esaCache.generateCache(dataset.toList)
  }
}


object AllDistances {
  def main(args: Array[String]) = {

    for(name <- List("ss", "ns", "nn")) {

      for(graph <- List("symGraph")) {

        val dataset = new NYTDataset(Configuration.nyt(name))
        val distanceAnalyzer = new AllDistanceAnalyzer(dataset, WikiGraphFactory.makeWikiGraph(graph))

        val path = "/tmp/%s_%s_dist.csv".format(graph, name)
        distanceAnalyzer.computeDistances(path)
      }
    }
  }

}


object MTX  {
  def main(args: Array[String]) = {
    val mtx = new WikiMTX("/data/ponza/graph")
    mtx.serializeGraph()
    mtx.serializeDictionary()
  }
}



object CC {
  def main(args: Array[String]) = {
    val scc = StronglyConnectedComponents.compute(WikiGraphFactory.outGraph.graph, false, null)

    println("Number of components: %d".format(scc.numberOfComponents))


    val sccs = scc.component.zipWithIndex.groupBy {
      case (ccID, nodeID) => ccID
    }.toList.sortBy(_._2.length).reverse

    sccs.slice(0, 1000).foreach {
      case (ccsID, nodeIDs) => println("Component %d with size %d".format(ccsID, nodeIDs.length))
    }
  }
}


object Mapping  {
  def main(args: Array[String]) = {

    val b = WikiTypeMapping.types("Silvio_Berlusconi").toArray().map(_.toString).foreach(println(_))
    println(WikiTypeMapping.types("New_York").toArray().map(_.toString).foreach(println(_)))
  }
}




object NYTMerging  {
  def main(args: Array[String]) = {

    for (name <- List("ss", "ns", "nn")) {
      val dataset = new NYTDataset(Configuration.nyt(name))
      val nytMerger = new NYTMerger(dataset, getDistanceFileName(Configuration.nyt(name)))
      nytMerger.mergeNYTWithDistances(Configuration.nyt(name) + ".merged")
    }
  }



  def getDistanceFileName(salience: String) = {
    val sal = salience.splitAt(salience.lastIndexOf("/") + 1)._2.split("\\.")(0)
    Paths.get(new File(salience).getParentFile.toString, "/distances/sym/symGraph_%s_dist.csv".format(sal)).toString
  }
}







/*
object WordBench {
  def main(args: Array[String]) {
    val esa = new ESARelatedness(Map("conceptThreshold" -> 625))

    val dataset = new WikiSimDataset(Configuration.dataset("wikiSim"))

    val benchmark = new WordSimBenchmark(dataset, esa)
    benchmark.runBenchmark()
  }
}


object ESAGrid {
  def main(args: Array[String]) {
    for{
      th <- 100 to 1000 by 100
    } {
        println("**************")
        val esa = new ESARelatedness(Map("conceptThreshold" -> th))
        println(esa.toString())

        val dataset = new WikiSimDataset(Configuration.dataset("procWikiSim"))

        val benchmark = new WordSimBenchmark(dataset, esa)
        benchmark.runBenchmark()
    }
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
        csrJson = """{"relatedness": "%s", "iters": %d, "decay": %1.3f, "graph": "%s"}"""
          .format(algo, iters, decay, graph)

      Bench.main(Array(csrJson))
    }
  }
}



object AllBench {
  val jsons = Array(
    """{"relatedness": "MilneWitten"}""",
    """{"relatedness": "Jaccard"}""",

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

object ProcessStandardLucene {
  def main(args: Array[String]): Unit = {
    val lucene = new LuceneProcessing()
    lucene.process()
  }
}
*/

object ProcessLemmaLucene {
  def main(args: Array[String]): Unit = {
    println("Lucene processing with lemmatization...")
    val lucene = new LemmaLuceneProcessing()
    lucene.process()
  }
}




/*
object ExtractsWikiIDs {
  def main(args: Array[String]): Unit = {
    val wikiDataset = new WikiSimDataset(Configuration.dataset("procWikiSim"))

    val csv = wikiDataset.map(task => "%d,%d".format(task.src.wikiID, task.dst.wikiID)) mkString "\n"
    new PrintWriter("/tmp/wikiPairs.csv") { write(csv); close() }
  }
}


object Cosine {
  def main(args: Array[String]): Unit = {
    Similarity.testCosine()
  }
}*/