package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.subgraph.topk

import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.webgraph.{ImmutableGraph, ImmutableSubgraph}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.subgraph.SubWikiBVGraph

/**
  * Class which groups common methods for subgraph generation via vector representation,
  * such as "top-k most similar entities" (ESA, w2v, ...).
  *
  * Subclasses have just to override neighborhood method. Differently from the original
  * SubWikiGraph class, here the neighborhood MUST return WikipediaIDs (NOT nodeIDs).
  */
abstract class TopKSubWikiBVGraph(srcWikiID: Int, dstWikiID: Int, wikiGraph: WikiBVGraph,
                                  val threshold: Int = 1000)
  extends SubWikiBVGraph(srcWikiID, dstWikiID, wikiGraph){


  /**
    *
    * @param path Ignored.
    * @return Induced graph from the neighborhood of srcWikiID and dstWikiID.
    */
  override def loadImmutableGraph(path: String) : ImmutableGraph = {
    logger.info("Generating subgraph starting from %d and %d...".format(srcWikiID, dstWikiID))

    val srcNodes = neighborhood(srcWikiID).map(wikiID => wikiGraph.getNodeID(wikiID))
    val dstNodes = neighborhood(dstWikiID).map(wikiID => wikiGraph.getNodeID(wikiID))

    val subNodes = subGraphNodes(superWikiGraph().getNodeID(srcWikiID),
                                  superWikiGraph().getNodeID(dstWikiID),
                                  srcNodes, dstNodes)

    val subGraph = new ImmutableSubgraph(wikiGraph.graph, subNodes.sorted)

    logger.info("Subgraph generated with %d nodes.".format(subGraph.numNodes))

    subGraph
  }


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
                    srcSimNodeIDs: Array[Int], dstSimNodeIDs: Array[Int]) : Array[Int] = {
    val nodes = new IntOpenHashSet()

    nodes.add(srcNodeID)
    nodes.add(dstNodeID)

    var (i, j) = (0, 0)
    var n = nodes.size()

    while(i < srcSimNodeIDs.length && j < dstSimNodeIDs.length && nodes.size() < threshold) {

      while (i < srcSimNodeIDs.length && n == nodes.size() && nodes.size() < threshold) {
        nodes.add( srcSimNodeIDs(i) )
        i += 1
      }
      if (n != nodes.size()) n += 1

      while (j < dstSimNodeIDs.length && n == nodes.size() && nodes.size() < threshold) {
        nodes.add( dstSimNodeIDs(j) )
        j += 1
      }
      if (n != nodes.size()) n += 1

    }

    // If previous loop ended for either i or j, it will enter just into ONLY one of these loops.
    while (i < srcSimNodeIDs.length && nodes.size() < threshold) { nodes.add( srcSimNodeIDs(i) ) ; i += 1 }
    while (j < dstSimNodeIDs.length && nodes.size() < threshold) { nodes.add( dstSimNodeIDs(j) ) ; j += 1 }

    nodes.toIntArray
  }

}
