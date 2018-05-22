package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.graph

import it.unimi.dsi.fastutil.ints.{Int2ObjectOpenHashMap, IntArrayList, IntOpenHashSet}
import org.slf4j.LoggerFactory

class WikiGraph(graph: Int2ObjectOpenHashMap[IntArrayList]) {

  private val logger = LoggerFactory.getLogger(classOf[WikiGraph])
  private lazy val numNodes = allDistinctWikiIDs


  /**
    * Returns out-degree of wikiID in graph.
    * @param wikiID
    * @return
    */
  def degree(wikiID: Int) = { graph.get(wikiID).size() }


  /**
    * Returns the list of wikiIDs which are linked to wikiID. wikiIDs MUST be sorted in non-decreasing order.
    * @param wikiID
    * @return
    */
  def edges(wikiID: Int) : IntArrayList = { if (graph.containsKey(wikiID)) graph.get(wikiID) else new IntArrayList() }


  /**
    * Computes the number of distinct nodes in the graph
    * @return
    */
  private def allDistinctWikiIDs : Int = {
    val wikiIDs = new IntOpenHashSet()

    wikiIDs.addAll(graph.keySet())
    graph.keySet().stream().forEach(wikiID => wikiIDs.addAll( edges(wikiID) ))

    wikiIDs.size()
  }


  /**
    * Computes the number of edges in the graph.
    * @return
    */
  def allEdges() : Int = {

    var numEdges = 0
    graph.keySet().stream().forEach(
      src => edges(src).stream().map[(Int, Int)](dst => (src.toInt, dst.toInt))
        .forEach(srcDst => numEdges += 1 )  )

    numEdges
  }


  /**
    * Returns the number of distinct nodes in the graph.
    * @return
    */
  def getNumNodes : Int = { numNodes }

}
