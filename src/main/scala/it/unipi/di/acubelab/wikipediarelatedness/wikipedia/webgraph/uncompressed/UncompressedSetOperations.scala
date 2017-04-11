package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.uncompressed

import it.unimi.dsi.fastutil.ints.IntArrayList


class UncompressedSetOperations(val wikiGraph: UncompressedWikiBVGraph) {


  def intersectionSize(srcWikiID: Int, dstWikiID: Int) : Int = {

    if(wikiGraph.outdegree(srcWikiID) == 0 || wikiGraph.outdegree(dstWikiID) == 0) return 0

    val a = wikiGraph.successorArray(srcWikiID)
    val b = wikiGraph.successorArray(dstWikiID)

    var i = 0
    var j = 0
    var intersection = 0

    do {

      if (a(i) == b(j)) {
        intersection += 1
        i += 1
        j += 1
      }

      while (i < a.length && j < b.length && a(i) < b(j)) i += 1
      while (i < a.length && j < b.length && b(j) < a(i)) j += 1

    } while (i < a.length && j < b.length)

    intersection
  }


  def unionSize(srcWikiID: Int, dstWikiID: Int) : Int = {
    val srcArray = wikiGraph.successorArray(srcWikiID)
    val dstArray = wikiGraph.successorArray(dstWikiID)

    new IntArrayList((srcArray ++ dstArray).distinct).size()
  }

}
