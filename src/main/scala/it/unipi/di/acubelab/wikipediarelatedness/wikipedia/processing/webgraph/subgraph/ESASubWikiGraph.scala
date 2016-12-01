package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.subgraph

import it.unimi.dsi.fastutil.ints.{IntArrayList, IntOpenHashSet}
import it.unimi.dsi.webgraph.{ImmutableGraph, ImmutableSubgraph}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa.ESA
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraphFactory

/**
  * Builds subgraph according to Top-K ESA concepts between srcWikiID and dstWikiID.
  * Threshold is the maximum size (number of nodes) of the graph.
  * @param srcWikiID
  * @param dstWikiID
  */
class ESASubWikiGraph(srcWikiID: Int, dstWikiID: Int, threshold: Int) extends SubWikiGraph(srcWikiID, dstWikiID) {

  override def loadImmutableGraph(path: String) : ImmutableGraph = {
    logger.info("Generating subgraph starting from %d and %d...".format(srcWikiID, dstWikiID))

    val srcNodes = neighborhood(srcWikiID)
    val dstNodes = neighborhood(dstWikiID)

    val subNodes = subGraphNodes(superWikiGraph().getNodeID(srcWikiID), superWikiGraph().getNodeID(dstWikiID),
                       srcNodes, dstNodes)

    val subGraph = new ImmutableSubgraph(WikiGraphFactory.outGraph.graph, subNodes.sorted)

    logger.info("Subgraph generated with %d nodes.".format(subGraph.numNodes))

    subGraph
  }


  override def neighborhood(wikiID: Int) : Array[Int] = {
    val neighWikiIDs = ESA.wikipediaConcepts(wikiID, threshold).map(_._1)
    neighWikiIDs.toArray
    //neighWikiIDs.map(wID => outGraph.getNodeID(wID)).toArray
  }

  /**
    * Compute the nodes used in the subgraph  by keeping top-threshold nodes from
    * the nodes specified in the function's parameters.
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
    var n = 0
    while(i < srcSimNodeIDs.length && j < dstSimNodeIDs.length && n < threshold) {
      val s = srcSimNodeIDs(i)
      val d = dstSimNodeIDs(j)

      if (s == d) {
        n += 1

        nodes.add(s)
      } else {
        n += 2
        nodes.add(s)
        nodes.add(d)
      }


      i += 1
      j += 1
    }

    nodes.toIntArray
  }
  
}