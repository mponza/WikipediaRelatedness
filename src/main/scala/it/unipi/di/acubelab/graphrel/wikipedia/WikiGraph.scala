package it.unipi.di.acubelab.graphrel.wikipedia

import it.unimi.dsi.webgraph.BVGraph
import it.unipi.di.acubelab.graphrel.utils.Configuration

object WikiGraph {
  lazy val outGraph = BVGraph.load(Configuration.wikipedia.outBVGraph)
  lazy val inGraph = BVGraph.load(Configuration.wikipedia.outBVGraph)
  lazy val symGraph = BVGraph.load(Configuration.wikipedia.symBVGraph)
  lazy val noLoopSymGraph = BVGraph.load(Configuration.wikipedia.noLoopSymBVGraph)


  /**
    *
    * @param bvGraph
    * @param srcWikiID
    * @param dstWikiID
    * @return Number of nodes which are both in the adjacent list of srcWikiID and dstWikiID.
    */
  def linkIntersection(bvGraph: BVGraph, srcWikiID: Int, dstWikiID: Int) : Int = {
    val iterA = inGraph.successors(srcWikiID)
    val iterB = inGraph.successors(dstWikiID)

    var intersection = 0
    var a = iterA.nextInt
    var b = iterB.nextInt

    while (a == b && a != -1) {
      intersection += 1
      a = iterA.nextInt
      b = iterB.nextInt
    }

    intersection
  }
}
