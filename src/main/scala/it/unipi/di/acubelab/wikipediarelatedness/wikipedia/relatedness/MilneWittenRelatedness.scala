package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.options.MilneWittenOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.WikiGraph
import org.slf4j.LoggerFactory

class MilneWittenRelatedness(options: MilneWittenOptions) extends Relatedness {
  val graph = WikiGraph.wikiBVGraph(options.graph)
  val W = graph.bvGraph.numNodes
  val logger = LoggerFactory.getLogger(classOf[MilneWittenRelatedness])

  /**
    * Paper: https://www.aaai.org/Papers/Workshops/2008/WS-08-15/WS08-15-005.pdf
    */
  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    val sizeA = graph.outdegree(srcWikiID)
    val sizeB = graph.outdegree(dstWikiID)

    val intersection = graph.linkIntersection(srcWikiID, dstWikiID)

    if (intersection == 0) return 0.0f

    val rel = (math.log(sizeA max sizeB) - math.log(intersection) ) /
                (math.log(W) - math.log(sizeA min sizeB))

    val normRel = 1 - ((rel.toFloat max 0.0f) min 1.0f)

    normRel
  }

  override def toString () : String = { "MilneWitten" }
}
