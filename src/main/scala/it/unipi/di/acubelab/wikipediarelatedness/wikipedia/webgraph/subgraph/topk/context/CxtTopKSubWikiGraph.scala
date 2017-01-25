package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.subgraph.topk.context

import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.webgraph.ImmutableGraph
import it.unimi.dsi.webgraph.ImmutableSubgraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.subgraph.topk.TopKSubWikiGraph

/**
  * Top-K from src and dst + their context. A subClass has to implement neighborhood and contextNodes methods.
 *
  * @param srcWikiID
  * @param dstWikiID
  * @param wikiGraph
  * @param threshold
  */
abstract class CxtTopKSubWikiGraph(srcWikiID: Int, dstWikiID: Int,
                                   wikiGraph: WikiGraph, threshold: Int = 1000)
          extends TopKSubWikiGraph(srcWikiID, dstWikiID, wikiGraph, threshold) {

  override def loadImmutableGraph(path: String) : ImmutableGraph = {
    logger.info("Generating context-subgraph starting from %d and %d...".format(srcWikiID, dstWikiID))

    val srcNodes = neighborhood(srcWikiID).map(wikiID => wikiGraph.getNodeID(wikiID))
    val dstNodes = neighborhood(dstWikiID).map(wikiID => wikiGraph.getNodeID(wikiID))
    val context = contextNodes(srcWikiID, dstWikiID).map(wikiID => wikiGraph.getNodeID(wikiID))

    val subNodes = subGraphNodes(superWikiGraph().getNodeID(srcWikiID),
      superWikiGraph().getNodeID(dstWikiID),
      srcNodes, dstNodes,
      context)

    val subGraph = new ImmutableSubgraph(wikiGraph.graph, subNodes.sorted)

    logger.info("Subgraph generated with %d nodes.".format(subGraph.numNodes))

    subGraph
  }


  /**
    * Returns a set of nodes which represent an "abstract context" bewtween srcWikiID and dstWikiID.
 *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  def contextNodes(srcWikiID: Int, dstWikiID: Int): Array[Int]



  /**
    * Computes the nodes used in the subgraph  by keeping top-threshold nodes from
    * the nodes specified in the function's parameters.
    *
    * @param srcNodeID
    * @param dstNodeID
    * @param srcSimNodeIDs
    * @param dstSimNodeIDs
    * @return Array of nodeIDs of size threshold.
    */
  def subGraphNodes(srcNodeID: Int, dstNodeID: Int,
                    srcSimNodeIDs: Array[Int], dstSimNodeIDs: Array[Int],
                    context: Array[Int]) : Array[Int] = {

    val nodes = new IntOpenHashSet()

    nodes.add(srcNodeID)
    nodes.add(dstNodeID)

    var (i, j, c) = (0, 0, 0)
    var n = nodes.size()

    while(i < srcSimNodeIDs.length && j < dstSimNodeIDs.length && nodes.size() < threshold && c < context.length) {

      // src
      while (i < srcSimNodeIDs.length && n == nodes.size() && nodes.size() < threshold) {
        nodes.add( srcSimNodeIDs(i) )
        i += 1
      }
      if (n != nodes.size()) n += 1

      // dst
      while (j < dstSimNodeIDs.length && n == nodes.size() && nodes.size() < threshold) {
        nodes.add( dstSimNodeIDs(j) )
        j += 1
      }
      if (n != nodes.size()) n += 1

      // context
      while (c < context.length && n == nodes.size() && nodes.size() < threshold) {
        nodes.add( context(c) )
        c += 1
      }
      if (n != nodes.size()) n += 1
    }

    // If previous loop ended for either i or j or c, it will enter just into ONLY one of these loops.
    while (i < srcSimNodeIDs.length && nodes.size() < threshold) { nodes.add( srcSimNodeIDs(i) ) ; i += 1 }
    while (j < dstSimNodeIDs.length && nodes.size() < threshold) { nodes.add( dstSimNodeIDs(j) ) ; j += 1 }
    while (c < context.length && nodes.size() < threshold) { nodes.add( context(c) ) ; c += 1 }

    nodes.toIntArray
  }



}
