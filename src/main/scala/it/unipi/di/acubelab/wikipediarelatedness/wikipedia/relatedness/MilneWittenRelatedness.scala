package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelTask
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.WikiGraph
import org.slf4j.LoggerFactory

class MilneWittenRelatedness(options: Map[String, Any]) extends Relatedness {
  val graphName = if (options.contains("graph")) options("name").toString else "inGraph"
  val graph = WikiGraph.wikiBVGraph(graphName)

  val W = graph.bvGraph.numNodes

  val logger = LoggerFactory.getLogger(classOf[MilneWittenRelatedness])

  /**
    * Paper: https://www.aaai.org/Papers/Workshops/2008/WS-08-15/WS08-15-005.pdf
    *
    * @return
    */
  def computeRelatedness(wikiRelTask: WikiRelTask) : Double = {
    val srcWikiID = wikiRelTask.src.wikiID
    val dstWikiID = wikiRelTask.dst.wikiID

    val sizeA = graph.outdegree(srcWikiID)
    val sizeB = graph.outdegree(dstWikiID)

    val intersection = graph.linkIntersection(srcWikiID, dstWikiID)

    if (intersection == 0) return 0.0

    val rel = (math.log(sizeA max sizeB) - math.log(intersection) ) /
                (math.log(W) - math.log(sizeA min sizeB))
    val normRel = 1 - ((rel max 0.0) min 1.0)

    normRel
  }

  override def toString () : String = { "MilneWitten" }
}
