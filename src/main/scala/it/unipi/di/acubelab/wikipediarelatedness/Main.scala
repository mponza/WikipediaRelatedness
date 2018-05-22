package it.unipi.di.acubelab.wikipediarelatedness

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.graph.WikiGraphProcessor
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

