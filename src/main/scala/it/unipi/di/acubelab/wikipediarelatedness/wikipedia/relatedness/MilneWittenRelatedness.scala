package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.options.MilneWittenOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.SetOperations
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraphFactory
import org.slf4j.LoggerFactory

class MilneWittenRelatedness(options: MilneWittenOptions) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[MilneWittenRelatedness])

  val wikiGraph = WikiGraphFactory.wikiBVGraph(options.graph)
  val W = setOperations.wikiGraph.graph.numNodes

  val setOperations = new SetOperations(wikiGraph)


  /**
    * Paper: https://www.aaai.org/Papers/Workshops/2008/WS-08-15/WS08-15-005.pdf
    */
  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    val sizeA = wikiGraph.outdegree(srcWikiID)
    val sizeB = wikiGraph.outdegree(dstWikiID)

    val intersection = setOperations.linkIntersection(srcWikiID, dstWikiID)

    if (intersection == 0) return 0.0f

    val rel = (math.log(sizeA max sizeB) - math.log(intersection) ) /
                (math.log(W) - math.log(sizeA min sizeB))

    val normRel = 1 - ((rel.toFloat max 0.0f) min 1.0f)

    normRel
  }

  override def toString () : String = { "MilneWitten" }
}
