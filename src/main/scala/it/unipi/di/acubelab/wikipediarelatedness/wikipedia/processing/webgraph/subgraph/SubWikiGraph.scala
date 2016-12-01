package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.subgraph

import it.unimi.dsi.fastutil.ints.{IntArrayList, IntOpenHashSet}
import it.unimi.dsi.webgraph.{ImmutableGraph, ImmutableSubgraph}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.{WikiGraph, WikiGraphFactory}
import org.slf4j.LoggerFactory


/**
  * Generates a subgraph built from several superGraphs (in, out).
  * Warning: all superBVgraph have to use the same wikiID-nodeID mapping.
  *
  * @param srcWikiID
  * @param dstWikiID
  */
abstract class SubWikiGraph(val srcWikiID: Int, val dstWikiID: Int)
    extends WikiGraph("") {

  // Respect with the WikiGraph class, here the field graph is an ImmutableSubGraph generated from superBVGraphs.
  // Warning: Keep attention how you map wikiID to nodeID!

  protected val inGraph = WikiGraphFactory.inGraph
  protected val outGraph = WikiGraphFactory.outGraph

  override val logger = getLogger()

  def getLogger() = LoggerFactory.getLogger(classOf[SubWikiGraph])

  /**
    * @param path Ignored.
    * @return Induced graph from the neighborhood of srcWikiID and dstWikiID.
    */
  override def loadImmutableGraph(path: String) : ImmutableGraph = {
    logger.info("Generating subgraph starting from %d and %d...".format(srcWikiID, dstWikiID))

    val srcNodes = neighborhood(srcWikiID)
    val dstNodes = neighborhood(dstWikiID)

    val subgraphNodes = new IntOpenHashSet()

    subgraphNodes.add(superWikiGraph().getNodeID(srcWikiID))
    subgraphNodes.add(superWikiGraph().getNodeID(dstWikiID))

    subgraphNodes.addAll(new IntArrayList(srcNodes))
    subgraphNodes.addAll(new IntArrayList(dstNodes))

    val subGraph = new ImmutableSubgraph(WikiGraphFactory.outGraph.graph, subgraphNodes)

    logger.info("Subgraph generated with %d nodes.".format(subGraph.numNodes))

    subGraph
  }



  /**
   * Generates the subgraph neighborhood of wikiID.
   *
   * @return NodeIDs of outGraph.
   */
  def neighborhood(wikiID: Int) : Array[Int]


  protected def subWikiGraph() : ImmutableSubgraph = graph.asInstanceOf[ImmutableSubgraph]


  protected def superWikiGraph() : WikiGraph = outGraph


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
