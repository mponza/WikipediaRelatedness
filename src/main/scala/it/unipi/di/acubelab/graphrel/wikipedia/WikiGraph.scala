package it.unipi.di.acubelab.graphrel.wikipedia

import it.unimi.dsi.fastutil.ints.{IntArrayList, IntOpenHashSet}
import it.unimi.dsi.webgraph.{BVGraph, LazyIntIterator}
import it.unipi.di.acubelab.graphrel.utils.Configuration

import scala.collection.mutable

object WikiGraph {
  lazy val outGraph = BVGraph.load(Configuration.wikipedia.outBVGraph)
  lazy val inGraph = BVGraph.load(Configuration.wikipedia.outBVGraph)
  lazy val symGraph = BVGraph.load(Configuration.wikipedia.symBVGraph)
  lazy val noLoopSymGraph = BVGraph.load(Configuration.wikipedia.noLoopSymBVGraph)


  /**
    *
    * @param graph
    * @param srcWikiID
    * @param dstWikiID
    * @return Number of nodes which are both in the adjacent list of srcWikiID and dstWikiID.
    */
  def linkIntersection(graph: BVGraph, srcWikiID: Int, dstWikiID: Int) : Int = {
    val iterA = graph.successors(srcWikiID)
    val iterB = graph.successors(dstWikiID)

    var intersection = 0
    var a = iterA.nextInt
    var b = iterB.nextInt

    do {
      if (a == b) {
        intersection += 1
        a = iterA.nextInt
        b = iterB.nextInt
      }

      // Aligns iterators to their minimum common element (if any).
      while (a < b && a != -1) a = iterA.nextInt
      while (b < a && b != -1) b = iterB.nextInt

    } while(a != -1 && b != -1)

    println(intersection)
    intersection
  }
}
