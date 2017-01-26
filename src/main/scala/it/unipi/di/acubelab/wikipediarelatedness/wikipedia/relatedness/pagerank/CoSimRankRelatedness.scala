package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank

import it.unipi.di.acubelab.wikipediarelatedness.options.CoSimRankOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.pagerank.cosimrank.{CoSimRankParallelGaussSeidel, CoSimRankPowerSeries}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory

/**
  * CoSimRank Relatedness on the whole Wikipedia graph.
 *
  * @param options
  */
class CoSimRankRelatedness(options: CoSimRankOptions = new CoSimRankOptions()) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[CoSimRankRelatedness])

  val csr = new CoSimRankParallelGaussSeidel(WikiBVGraphFactory.inWikiBVGraph, options.iterations, options.pprDecay, options.csrDecay)


  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    csr.similarity(srcWikiID, dstWikiID)
  }


  override def toString(): String = {
    "CoSimRank_%s".format(options)
  }
}
