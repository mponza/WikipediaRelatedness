package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraph

/**
  * Class which implements set-based operations over node neighborhoods of a wikiBVGraph.
  *
  * @param wikiGraph
  */
class SetOperations(val wikiGraph: WikiBVGraph) {


  /**
    * Computes the size of the intersection between the neighborhood of srcWikiID and dstWikiID in wikiGraph
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  def intersectionSize(srcWikiID: Int, dstWikiID: Int) : Int = {

    // using successorArrays does not change time performance
    val iterA =  wikiGraph.graph.successors( wikiGraph.wiki2node.get(srcWikiID) )
    val iterB = wikiGraph.graph.successors( wikiGraph.wiki2node.get(dstWikiID) )

    var intersection = 0
    var a = iterA.nextInt
    var b = iterB.nextInt

    do {
      if (a == b && a != -1) {
        intersection += 1
        a = iterA.nextInt
        b = iterB.nextInt
      }

      while (a < b && a != -1 && b != -1) a = iterA.nextInt
      while (b < a && b != -1 && a != -1) b = iterB.nextInt

    } while(a != -1 && b != -1)


    intersection
  }


  /**
    *  Computes the union between the neighborhood of srcWikiID and dstWikiID in wikiGraph
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  def union(srcWikiID: Int, dstWikiID: Int) : IntArrayList = {
    val srcArray = wikiGraph.successorArray(srcWikiID)
    val dstArray = wikiGraph.successorArray(dstWikiID)

    new IntArrayList((srcArray ++ dstArray).distinct)
  }


  /**
    * Returns the intersected wikiIDs between srcWikiId and dstWikiId in wikiGraph.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  def intersection(srcWikiID: Int, dstWikiID: Int) : IntArrayList = {

    val iterA =  wikiGraph.successors(srcWikiID)
    val iterB = wikiGraph.successors(dstWikiID)

    val intersection = new IntArrayList()
    var a = iterA.nextInt
    var b = iterB.nextInt

    do {
      if (a == b) {
        intersection.add(a)
        a = iterA.nextInt
        b = iterB.nextInt
      }


      while (a < b && a != -1) a = iterA.nextInt
      while (b < a && b != -1) b = iterB.nextInt

    } while(a != -1 && b != -1)

    intersection
  }

}
