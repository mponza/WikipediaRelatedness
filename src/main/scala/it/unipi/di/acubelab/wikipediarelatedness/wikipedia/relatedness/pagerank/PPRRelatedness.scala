package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms.ppr.PersonalizedPageRank
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph.{WikiJungGraph, WikiJungGraphFactory}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.ops.transforms.Transforms
import org.slf4j.LoggerFactory


class PPRRelatedness(options: RelatednessOptions) extends Relatedness {
  protected val logger = LoggerFactory.getLogger(getClass)

  val wikiJungGraph = WikiJungGraphFactory.make("bv")


  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = {
    val srcPPR = pprVector(srcWikiID)
    val dstPPR = pprVector(dstWikiID)

    Transforms.cosineSim(srcPPR, dstPPR).toFloat
  }


  protected def pprVector(wikiID: Int) : INDArray = {
    val ppr = new PersonalizedPageRank(wikiJungGraph, wikiID, options.pprDecay, options.iterations)
    ppr.computePPRVectors().getColumn(options.iterations - 1)
  }
}
