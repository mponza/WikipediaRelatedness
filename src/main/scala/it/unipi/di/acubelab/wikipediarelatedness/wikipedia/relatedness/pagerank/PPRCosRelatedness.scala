package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.pagerank.cosimrank.CoSimRankPowerSeries
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraphFactory
import org.slf4j.LoggerFactory

class PPRCosRelatedness {
  val logger = LoggerFactory.getLogger(classOf[CoSimRankRelatedness])
  //val csr = new CoSimRankGaussSeidel(WikiGraphFactory.outGraph, options.iterations, options.pprDecay, options.csrDecay)
  val csr = new CoSimRankPowerSeries(WikiGraphFactory.outGraph, options.iterations, options.pprDecay, options.csrDecay)


  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {

    val s = csr.similarity(srcWikiID, dstWikiID)
    println("%d %d %1.5f".formatLocal(java.util.Locale.US, srcWikiID, dstWikiID, s))
    s
  }

  override def toString(): String = {
    "CoSimRank_%s".format(options)
  }
}
