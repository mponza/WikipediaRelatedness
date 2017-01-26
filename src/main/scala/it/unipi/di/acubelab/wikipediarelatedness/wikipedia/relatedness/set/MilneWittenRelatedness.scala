package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.set

import it.unipi.di.acubelab.wikipediarelatedness.options.MilneWittenOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.SetOperations
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory

class MilneWittenRelatedness(options: MilneWittenOptions = new MilneWittenOptions()) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[MilneWittenRelatedness])

  val wikiGraph = WikiBVGraphFactory.makeWikiGraph(options.graph)
  val setOperations = new SetOperations(wikiGraph)
  val W = setOperations.wikiGraph.graph.numNodes


  /**
    * Paper: https://www.aaai.org/Papers/Workshops/2008/WS-08-15/WS08-15-005.pdf
    */
  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    val sizeA = wikiGraph.outdegree(srcWikiID)
    val sizeB = wikiGraph.outdegree(dstWikiID)

    val intersection = setOperations.intersectionSize(srcWikiID, dstWikiID)

    if (intersection == 0) return 0f

    val rel = (math.log(sizeA max sizeB) - math.log(intersection) ) /
                (math.log(W) - math.log(sizeA min sizeB))

    val normRel = 1 - ((rel.toFloat max 0f) min 1f)

    normRel
  }

  override def toString () : String = { "MilneWitten" }
}
