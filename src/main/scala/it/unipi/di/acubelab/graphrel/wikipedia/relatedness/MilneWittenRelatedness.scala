package it.unipi.di.acubelab.graphrel.wikipedia.relatedness

import it.unimi.dsi.webgraph.BVGraph
import it.unipi.di.acubelab.graphrel.wikipedia.WikiGraph
import org.slf4j.LoggerFactory

class MilneWittenRelatedness(graph: BVGraph, W: Int) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[MilneWittenRelatedness])

  logger.info("M&W Relatedness loaded with |W| = %d.".format(W))

  /**
    * Paper: https://www.aaai.org/Papers/Workshops/2008/WS-08-15/WS08-15-005.pdf
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Double = {
    val sizeA = graph.outdegree(srcWikiID)
    val sizeB = graph.outdegree(dstWikiID)

    val intersection = WikiGraph.linkIntersection(graph, srcWikiID, dstWikiID)

    (math.log(sizeA max sizeB) - math.log(intersection)) /
      math.log(W) - math.log(sizeA min sizeB)
  }

  def name() : String = { "MilneWitten" }
}
