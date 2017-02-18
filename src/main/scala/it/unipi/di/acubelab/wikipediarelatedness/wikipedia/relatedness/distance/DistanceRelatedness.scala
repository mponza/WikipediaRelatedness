package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.distance

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessOptions}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.WikiBVDistance
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory


/**
  * Distance based computation.
  * @param options
  */
class DistanceRelatedness(options: RelatednessOptions)  extends Relatedness {

  protected val logger = LoggerFactory.getLogger(getClass)

  protected val outDist = new WikiBVDistance( WikiBVGraphFactory.make(options.graph) )

  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    if (srcWikiID == dstWikiID) return 1f
    val srcDstDist = outDist.getDistance(srcWikiID, dstWikiID)
    val dstSrcDist = outDist.getDistance(dstWikiID, srcWikiID)

    1 /  (0.5f * (srcDstDist + dstSrcDist) + 1)
  }

  override def toString = "Distance_graph:%s".format(options.graph)
}
