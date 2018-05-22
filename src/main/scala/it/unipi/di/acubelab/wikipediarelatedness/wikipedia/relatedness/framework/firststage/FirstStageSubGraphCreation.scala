package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.firststage

import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.firststage.nodes.NodesOfSubGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.secondstage.weights.WeightsOfSubGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.subgraph.WikiSubGraph

/**
  * First Stage of the TwoStageFramework, i.e. selection of the top-k nodes that will be used for populating the SubGraph.
  * For efficiency reasons the weighting of edges is in the Second Stage.
  *
  * @param nodesOfSubGraph
  * @param k
  */
class FirstStageSubGraphCreation(nodesOfSubGraph: NodesOfSubGraph, k: Int) {

  def growWikipediaSubGraph(srcWikiID: Int, dstWikiID: Int): WikiSubGraph = {

    if (srcWikiID.equals(dstWikiID)) {
      // safe time, the Second Stage returns 1f when srcWikiID.equals(dstWikiID)
      return new WikiSubGraph(srcWikiID, dstWikiID, null)
    }

    val srcTopNodes = nodesOfSubGraph.topNodes(srcWikiID).slice(0, k)
    val dstTopNodes = nodesOfSubGraph.topNodes(dstTopNodes).slice(0, k)

    new WikiSubGraph(srcWikiID, dstWikiID,
                      chooseNodes(srcWikiID, dstWikiID, srcTopNodes, dstTopNodes)
                    )
  }


  /**
    * Given the top-k nodes for srcWikiID and dstWikiID, we choose (one by one) the top from both Seqs.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @param srcVec
    * @param dstVec
    */
  private def chooseNodes(srcWikiID: Int, dstWikiID: Int, srcVec: Seq[Int], dstVec: Seq[Int]): IntOpenHashSet = {
    val nodes = new IntOpenHashSet()
    nodes.add(srcWikiID)
    nodes.add(dstWikiID)

    val srcVec = Seq.empty[Int] // change
    val dstVec = Seq.empty[Int]

    var (i, j) = (0, 0)
    var n = nodes.size()

    while (i < srcVec.length && j < dstVec.length && nodes.size() < k) {

      while (i < srcVec.length && n == nodes.size() && nodes.size() < k) {
        nodes.add(srcVec(i))
        i += 1
      }
      if (n != nodes.size()) n += 1

      while (j < dstVec.length && n == nodes.size() && nodes.size() < k) {
        nodes.add(dstVec(j))
        j += 1
      }
      if (n != nodes.size()) n += 1

    }
    while (i < srcVec.length && nodes.size() < k) {
      nodes.add(srcVec(i)); i += 1
    }
    while (j < dstVec.length && nodes.size() < k) {
      nodes.add(dstVec(j)); j += 1
    }

    nodes
  }

}
