package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import it.unipi.di.acubelab.wikipediarelatedness.options.PPRCosOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.pagerank.pprcos.PPRCosParallelGaussSeidel
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraphFactory
import org.slf4j.LoggerFactory

class PPRCosRelatedness(options: PPRCosOptions) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[CoSimRankRelatedness])

  val pprcos = new PPRCosParallelGaussSeidel(WikiGraphFactory.inGraph, options.iterations, options.pprDecay)

  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    val rel = pprcos.similarity(srcWikiID, dstWikiID)
    println("Relatedness between %d %d %1.5f".format(srcWikiID, dstWikiID, rel))

    rel
  }

  override def toString(): String = {
    "PPRCos_%s".format(options)
  }
}
