package it.unipi.di.acubelab.wikipediarelatedness

import it.unipi.di.acubelab.wikipediarelatedness.evaluation.benchmark.WikiRelBenchmark
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.dataset.WikiRelDatasetFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.graph.WikiGraphProcessor
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.WikiRelatednessFactory
import org.slf4j.LoggerFactory


/**
  * Take as input a graph of out-edges in tsv format and save it in FastUtil format.
  */
object WikiGraphProcessing {

  val logger = LoggerFactory.getLogger("WikiGraphProcessing")

  def main(args: Array[String]): Unit = {

    val wikiGraphTSV = args(0)

    val wikiOutBin = args(1)
    val wikiInBin = args(2)
    val wikiSymBin = args(3)

    new WikiGraphProcessor().process( wikiGraphTSV, wikiOutBin, wikiInBin, wikiSymBin )
  }

}


object WikiRelBenchmarking {

  def main(args: Array[String]) : Unit = {


    val wire = WikiRelDatasetFactory.apply("WiRe")

    val graphFilename = args(0)
    val rel = WikiRelatednessFactory.makeMilneWitten(graphFilename)

    val benchmark = new WikiRelBenchmark(wire, rel)
    benchmark.run()

  }
}
