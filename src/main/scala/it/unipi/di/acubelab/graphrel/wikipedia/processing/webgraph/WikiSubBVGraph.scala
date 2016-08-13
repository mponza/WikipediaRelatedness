package it.unipi.di.acubelab.graphrel.wikipedia.processing.webgraph

import it.unipi.di.acubelab.graphrel.wikipedia.WikiGraph
import it.unimi.dsi.fastutil.ints.{Int2ObjectOpenHashMap, IntArrayList, IntOpenHashSet}
import it.unimi.dsi.webgraph.ImmutableSubgraph
import org.slf4j.LoggerFactory


class WikiSubBVGraph(val superBVGraphs: Map[String, WikiBVGraph], val srcWikiID: Int, val dstWikiID: Int) {
  val logger = LoggerFactory.getLogger(classOf[WikiSubBVGraph])
  val immSubGraph = subGraph(superBVGraphs, srcWikiID, dstWikiID)

  /**
    * Generate the subgraph made by a 1-level of BFS (depending on the specified superBVGraphs),
    * startng from srcWikiID and dstWikiID and then linking the generated nodes in the Wikipedia.outGraph.
    *
    * @param superBVGraphs Neighbrohood of srcWikiID and dstWikiID will be extracted from  these graphs.
    */
  def subGraph(superBVGraphs: Map[String, WikiBVGraph], srcWikiID: Int, dstWikiID: Int) : ImmutableSubgraph = {
    logger.info("Generating subgraph starting from %d and %d...".format(srcWikiID, dstWikiID))

    val srcNodes = neighbrohood(srcWikiID, superBVGraphs)
    val dstNodes = neighbrohood(dstWikiID, superBVGraphs)

    val subgraphNodes = new IntOpenHashSet()

    subgraphNodes.add(WikiBVGraph.getNodeID(srcWikiID))
    subgraphNodes.add(WikiBVGraph.getNodeID(dstWikiID))

    subgraphNodes.addAll(new IntArrayList(srcNodes))
    subgraphNodes.addAll(new IntArrayList(dstNodes))

    val subGraph = new ImmutableSubgraph(WikiGraph.outGraph.bvGraph, subgraphNodes)

    logger.info("Subgraph generated with %d nodes."
      .format(subGraph.numNodes))

    subGraph
  }

  def neighbrohood(srcWikiID: Int, superBVGraphs: Map[String, WikiBVGraph]) : Array[Int] = {
    superBVGraphs.flatMap {
      case (graphName, bvGraph) => bvGraph.successorArray(srcWikiID)
    }.toArray.distinct
  }

  def super2subNodeID(superNodeID: Int) : Int = {
    immSubGraph.fromSupergraphNode(superNodeID)
  }

  def getNodeID(wikiID: Int) : Int = {
    val superNodeID = WikiBVGraph.getNodeID(wikiID)
    super2subNodeID(superNodeID)
  }

  def sub2superNodeID(subNodeID: Int) : Int = {
    immSubGraph.toSupergraphNode(subNodeID)
  }

  def getWikiID(subNodeID: Int) : Int = {
    val superNodeID = sub2superNodeID(subNodeID)
    WikiBVGraph.getWikiID(superNodeID)
  }

  def outdegree(wikiID: Int) : Int = {
    immSubGraph.outdegree(getNodeID(wikiID))
  }

  /**
    * @return Edges as ArrayMap (src -> [dst]). All elements are are wikiIDs.
    * */
  def wikiEdges() : Int2ObjectOpenHashMap[IntArrayList] = {
    val edges = new Int2ObjectOpenHashMap[IntArrayList]

    for(i <- 0 until immSubGraph.numNodes()) {
      val wikiID = getWikiID(i)
      val succs = immSubGraph.successorArray(i).map(x => getWikiID(x))

      succs.foreach {
        dst => edges.putIfAbsent(wikiID, new IntArrayList)
               edges.get(wikiID).add(dst)
      }
    }

    edges
  }
}
