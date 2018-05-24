package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.graph

import it.unimi.dsi.fastutil.ints.{Int2ObjectOpenHashMap, IntArrayList, IntOpenHashSet}
import it.unimi.dsi.fastutil.io.BinIO
import org.slf4j.LoggerFactory

class WikiGraph(graph: Int2ObjectOpenHashMap[IntArrayList]) {

  private val logger = LoggerFactory.getLogger(classOf[WikiGraph])
  private val numNodes = allDistinctWikiIDs.size


  /**
    * Returns degree of wikiID in graph.
    *
    * @param wikiID
    * @return
    */
  def degree(wikiID: Int) = { if (graph.containsKey(wikiID)) graph.get(wikiID).size() else 0 }


  /**
    * Returns the list of wikiIDs which are linked to wikiID. wikiIDs MUST be sorted in non-decreasing order.
    *
    * @param wikiID
    * @return
    */
  def edges(wikiID: Int) : IntArrayList = { if (graph.containsKey(wikiID)) graph.get(wikiID) else new IntArrayList() }


  /**
    * Computes the number of distinct nodes in the graph
    *
    * @return
    */
  def allDistinctWikiIDs : Seq[Int] = {
    val wikiIDs = new IntOpenHashSet()

    wikiIDs.addAll(graph.keySet())
    graph.keySet().toIntArray.foreach(wikiID => wikiIDs.addAll( edges(wikiID) ))

    wikiIDs.toIntArray
  }





  /**
    * Computes the number of edges in the graph.
    *
    * @return
    */
  def countAllEdges() : Int = {

    var numEdges = 0
    graph.keySet().stream().forEach(
      src => edges(src).stream().map[(Int, Int)](dst => (src.toInt, dst.toInt))
        .forEach(srcDst => numEdges += 1 )  )

    numEdges
  }


  def allIterableEdges() : Iterable[(Int, Int)]= {
    for (
      srcWikiID <- graph.keySet().toIntArray();
      dstWikiID <- edges(srcWikiID).toIntArray()
    ) yield (srcWikiID, dstWikiID)
  }


  /**
    * Returns the number of distinct nodes in the graph.
    *
    * @return
    */
  def getNumNodes : Int = { numNodes }

}


object WikiGraph {
  private val logger = LoggerFactory.getLogger(classOf[WikiGraph])

  def apply(graphFilename: String): WikiGraph = {
    logger.info(s"Loading WikiGraph from ${graphFilename}...")

    val wikiGraph = new WikiGraph(
      BinIO.loadObject(graphFilename).asInstanceOf[Int2ObjectOpenHashMap[IntArrayList]]
    )

    logger.info("Done.")

    wikiGraph
  }
}