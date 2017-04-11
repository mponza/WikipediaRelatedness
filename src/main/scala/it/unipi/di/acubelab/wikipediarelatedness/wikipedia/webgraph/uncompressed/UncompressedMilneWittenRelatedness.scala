package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.uncompressed

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessOptions}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory


class UncompressedMilneWittenRelatedness (options: RelatednessOptions) extends Relatedness {
  val logger = LoggerFactory.getLogger(getClass)

  val wikiGraph = {
    val immInGraph = WikiBVGraphFactory.immInGraph
    val wiki2node = WikiBVGraphFactory.wiki2node
    new UncompressedWikiBVGraph(immInGraph, wiki2node)
  }

  val setOperations = new UncompressedSetOperations(wikiGraph)
  val W = wikiGraph.graph.numNodes

  // Enable these two lines for memoization
  //val memo = FastMemoize(relatedness)
  // override def computeRelatedness(srcWikiID: Int, dstWikiID: Int) = memo(srcWikiID, dstWikiID)


  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    if (srcWikiID == dstWikiID) return 1f // added

    if(!wikiGraph.wiki2node.containsKey(srcWikiID) || !wikiGraph.wiki2node.containsKey(dstWikiID)) {
      logger.warn("%d not present!" format srcWikiID )
      return 0f
    }

    if(!wikiGraph.wiki2node.containsKey(dstWikiID)) {
      logger.warn("%d not present!" format dstWikiID )
      return 0f
    }

    val sizeA = wikiGraph.outdegree(srcWikiID)
    val sizeB = wikiGraph.outdegree(dstWikiID)

    val intersection = setOperations.intersectionSize(srcWikiID, dstWikiID)

    if (intersection == 0) return 0f

    val rel = (math.log(sizeA max sizeB) - math.log(intersection) ) /
      (math.log(W) - math.log(sizeA min sizeB))

    val normRel = 1 - ((rel.toFloat max 0f) min 1f)



    normRel
  }


  override def toString () : String = { "UncompressedMilneWitten_graph:in" }
}
