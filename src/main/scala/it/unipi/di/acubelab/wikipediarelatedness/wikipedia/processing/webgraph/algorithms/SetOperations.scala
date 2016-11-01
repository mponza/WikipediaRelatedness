package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraph

/**
  * Class which implements set-based operations over node neighborhoods of a wikiBVGraph.
  *
  * @param wikiGraph
  */
class SetOperations(val wikiGraph: WikiGraph) {

  /**
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return Number of intersected nodes between srcWikiID and dstWikiID.
    */
  def intersectionSize(srcWikiID: Int, dstWikiID: Int) : Int = {
    val iterA =  wikiGraph.successors(srcWikiID)
    val iterB = wikiGraph.successors(dstWikiID)

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

    intersection
  }

  /**
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return wikiIDs which belong to the union between srcWikiID and dstWikiID.
    */
  def wikiUnion(srcWikiID: Int, dstWikiID: Int) : IntArrayList = {
    val srcArray = wikiGraph.successorArray(srcWikiID)
    val dstArray = wikiGraph.successorArray(dstWikiID)

    new IntArrayList((srcArray ++ dstArray).distinct.map(nodeID => wikiGraph.getWikiID(nodeID)))
  }


  /**
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return wikiIDs which belong to the intersection between srcWikiID and dstWikiID.
    */
  def wikiIntersection(srcWikiID: Int, dstWikiID: Int) : IntArrayList = {

    val iterA =  wikiGraph.successors(srcWikiID)
    val iterB = wikiGraph.successors(dstWikiID)

    val intersection = new IntArrayList()
    var a = iterA.nextInt
    var b = iterB.nextInt

    do {
      if (a == b) {
        intersection.add(wikiGraph.getWikiID(a))
        a = iterA.nextInt
        b = iterB.nextInt
      }

      // Aligns iterators to their minimum common element (if any).
      while (a < b && a != -1) a = iterA.nextInt
      while (b < a && b != -1) b = iterB.nextInt

    } while(a != -1 && b != -1)

    intersection
  }


}
