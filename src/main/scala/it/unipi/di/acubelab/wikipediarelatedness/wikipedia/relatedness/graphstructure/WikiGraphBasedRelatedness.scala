package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.graphstructure

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.graph.WikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.WikiRelatedness
import org.slf4j.LoggerFactory


/**
  * Abstract class for graph-based relatedness.
  *
  * @param wikiGraph
  */
abstract class WikiGraphBasedRelatedness(wikiGraph: WikiGraph) extends WikiRelatedness {

  private val logger = LoggerFactory.getLogger(classOf[WikiGraphBasedRelatedness])

  /**
    * Computes intersection between neighborhood of srcWikiID and dstWikiID in wikiGraph.
    * wikigraph.edges MUST returns wikiID non-decreasingly sorterd.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  def intersection(srcWikiID: Int, dstWikiID: Int): Int = {
    if (wikiGraph.degree(srcWikiID) == 0 || wikiGraph.degree(dstWikiID) == 0) return 0

    var iterA = wikiGraph.edges(srcWikiID).iterator()
    var iterB = wikiGraph.edges(dstWikiID).iterator()

    var intersection = 0
    var a = iterA.nextInt
    var b = iterB.nextInt

    do {
      if (a == b) {
        intersection += 1
        if(iterA.hasNext) a = iterA.nextInt
        if(iterB.hasNext) b = iterB.nextInt
      }

      while (iterA.hasNext && iterB.hasNext && a < b) a = iterA.nextInt
      while (iterA.hasNext && iterB.hasNext && b < a) b = iterB.nextInt

      if((!iterA.hasNext || !iterB.hasNext()) && a == b) {
        // one matched element but iteration is finished
        intersection += 1
      }

    } while(iterA.hasNext && iterB.hasNext)


    intersection
  }


  /**
    *  Computes the union between the neighborhood of srcWikiID and dstWikiID in wikiGraph
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  def union(srcWikiID: Int, dstWikiID: Int) : Int = {
    val srcArray = wikiGraph.edges(srcWikiID).toIntArray
    val dstArray = wikiGraph.edges(dstWikiID).toIntArray

    (srcArray ++ dstArray).distinct.size
  }
}
