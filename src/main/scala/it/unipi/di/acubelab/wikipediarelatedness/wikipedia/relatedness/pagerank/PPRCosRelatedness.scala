package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import it.unipi.di.acubelab.wikipediarelatedness.options.PPRCosOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.pagerank.pprcos.PPRCosParallelGaussSeidel
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory

class PPRCosRelatedness(options: PPRCosOptions) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[CoSimRankRelatedness])

  val pprcos = new PPRCosParallelGaussSeidel(WikiBVGraphFactory.inWikiBVGraph, options.iterations, options.pprDecay)

  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    val rel = pprcos.similarity(srcWikiID, dstWikiID)

    rel
  }

  override def toString(): String = {
    "PPRCos_%s".format(options)
  }
}
