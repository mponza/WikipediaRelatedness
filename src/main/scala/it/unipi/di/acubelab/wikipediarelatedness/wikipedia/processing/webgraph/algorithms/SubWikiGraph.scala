package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms

import it.unimi.dsi.fastutil.ints.{Int2ObjectOpenHashMap, IntArrayList, IntOpenHashSet}
import it.unimi.dsi.webgraph.{ImmutableGraph, ImmutableSubgraph}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.{WikiGraph, WikiGraphFactory}
import org.slf4j.LoggerFactory


/**
  * Generates a subgraph built from several superGraphs (in, out).
  * Warning: all superBVgraph have to use the same wikiID-nodeID mapping.
  *
  * @param superWikiGraphs
  * @param srcWikiID
  * @param dstWikiID
  */
class SubWikiGraph(val superWikiGraphs: List[WikiGraph] = List(WikiGraphFactory.inGraph, WikiGraphFactory.outGraph),
                   val srcWikiID: Int, val dstWikiID: Int) extends WikiGraph("") {
  override val logger = LoggerFactory.getLogger(classOf[SubWikiGraph])

  // The field graph is an ImmutableSubGraph generated from superBVGraphs.

  /**
    * @param path Ignored.
    * @return Induced graph from the neighborhood of srcWikiID and dstWikiID.
    */
  override def loadImmutableGraph(path: String) : ImmutableGraph = {
    logger.info("Generating subgraph starting from %d and %d...".format(srcWikiID, dstWikiID))

    val srcNodes = superNeighborhood(srcWikiID, superWikiGraphs)
    val dstNodes = superNeighborhood(dstWikiID, superWikiGraphs)

    val subgraphNodes = new IntOpenHashSet()

    subgraphNodes.add(superWikiGraph().getNodeID(srcWikiID))
    subgraphNodes.add(superWikiGraph().getNodeID(dstWikiID))

    subgraphNodes.addAll(new IntArrayList(srcNodes))
    subgraphNodes.addAll(new IntArrayList(dstNodes))

    val subGraph = new ImmutableSubgraph(WikiGraphFactory.outGraph.graph, subgraphNodes)

    logger.info("Subgraph generated with %d nodes.".format(subGraph.numNodes))

    subGraph
  }

  def superNeighborhood(srcWikiID: Int, superBVGraphs: List[WikiGraph]) : Array[Int] = {
    superBVGraphs.map(bvGraph => bvGraph.successorArray(srcWikiID)).toArray.flatten.distinct
  }


  protected def subWikiGraph() : ImmutableSubgraph = graph.asInstanceOf[ImmutableSubgraph]


  protected def superWikiGraph() : WikiGraph = superWikiGraphs(0)


  /**
    * @param wikiID
    * @return (Sub) graph's nodeID.
    */
  override def getNodeID(wikiID: Int) : Int = {
    val superNodeID = superWikiGraph().getNodeID(wikiID)
    subWikiGraph().fromSupergraphNode(superNodeID)
  }


  override def getWikiID(subNodeID: Int) : Int = {
    val superNodeID = subWikiGraph().toSupergraphNode(subNodeID)
    superWikiGraph().getWikiID(superNodeID)
  }


  override def outdegree(wikiID: Int) : Int = {
    subWikiGraph().outdegree(getNodeID(wikiID))
  }

}
