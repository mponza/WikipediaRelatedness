package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import it.unipi.di.acubelab.wikipediarelatedness.options.PPRCosOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.pagerank.cosimrank.CoSimRankPowerSeries
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraphFactory
import org.slf4j.LoggerFactory

class PPRCosRelatedness(options: PPRCosOptions) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[CoSimRankRelatedness])
  //val csr = new CoSimRankGaussSeidel(WikiGraphFactory.outGraph, options.iterations, options.pprDecay, options.csrDecay)
  val pprcos = new CoSimRankPowerSeries(WikiGraphFactory.outGraph, options.iterations, options.pprDecay)


  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    pprcos.similarity(srcWikiID, dstWikiID)
  }

  override def toString(): String = {
    "PPRCos_%s".format(options)
  }
}
