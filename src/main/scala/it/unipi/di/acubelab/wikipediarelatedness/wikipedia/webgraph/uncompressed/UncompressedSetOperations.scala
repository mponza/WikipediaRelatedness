package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.uncompressed



class UncompressedSetOperations(val wikiGraph: UncompressedWikiBVGraph) {


  def intersectionSize(srcWikiID: Int, dstWikiID: Int) : Int = {
    val a = wikiGraph.successorArray(srcWikiID)
    val b = wikiGraph.successorArray(dstWikiID)

    if (a.isEmpty || b.isEmpty) return 0

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

}
