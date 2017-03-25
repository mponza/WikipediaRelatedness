package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph

import it.unimi.dsi.fastutil.Arrays
import it.unimi.dsi.fastutil.ints.{Int2IntOpenHashMap, IntArrayList}
import it.unimi.dsi.webgraph.{ImmutableGraph, LazyIntIterator, LazyIntIterators}
import org.slf4j.LoggerFactory


/**
  * Wrapper of the Wikipedia graph managed as a BVGraph.
  * This class transparently manages the mapping between wikiID and nodeID.
  *
  */
class WikiBVGraph(val graph: ImmutableGraph, val wiki2node: Int2IntOpenHashMap) extends ImmutableGraph {
  val logger = LoggerFactory.getLogger(getClass)


  // WikiID -> NodeID mapping.
  lazy val node2wiki = reverseWiki2Node()
  protected val vertices = wiki2node.keySet().toIntArray()

  /**
    * Returns the wikiIDs successor of wikiID.
    *
    * @param wikiID
    * @return
    */
  override def successors(wikiID: Int): LazyIntIterator = {
    LazyIntIterators.wrap( successorArray(wikiID) )
  }


  /**
    * Returns the successors array of wikiIDs from wikiID.
    *
    * @param wikiID
    * @return
    */
  override def successorArray(wikiID: Int): Array[Int] = graph.successorArray( getNodeID(wikiID) )
                                                                .map( getWikiID(_) ).sorted


  /**
    * The degree of wikiID in the graph.
    *
    * @param wikiID
    * @return
    */
  def outdegree(wikiID: Int): Int = graph.outdegree( getNodeID(wikiID) )


  /**
    * Check if wikiID is contained in the graph.
    *
    * @param wikiID
    * @return
    */
  def contains(wikiID: Int): Boolean = wiki2node.containsKey(wikiID)


  /**
    * Reverses wiki2node (wikiID -> nodeID) to node2wiki (nodeID -> wikiID).
    *
    * @return
    */
  protected def reverseWiki2Node(): Int2IntOpenHashMap = {
    val node2wiki = new Int2IntOpenHashMap
    wiki2node.keySet().toIntArray.foreach {
      wikiID: Int => node2wiki.put(wiki2node.get(wikiID), wikiID)
    }
    node2wiki
  }


  /**
    * Returns the nodeID of wikiID.
    *
    * @param wikiID
    * @return
    */
  def getNodeID(wikiID: Int) : Int = {
    val nodeID = wiki2node.getOrDefault(wikiID, -1)
    if (nodeID < 0) throw new IllegalArgumentException("WikiID %d not present in the Wikipedia graph."
                                                          .format(wikiID))
    nodeID
  }


  /**
    * Returns the wikiID of nodeID.
    *
    * @param nodeID
    * @return
    */
  def getWikiID(nodeID: Int): Int = {
    val wikiID = node2wiki.getOrDefault(nodeID, -1)
    if (wikiID < 0) throw new IllegalArgumentException("NodeIndex %d not present in the Wikipedia mapping."
                                                          .format(nodeID))

    wikiID
  }


  /**
    * Returns the list of indexed wikiIDs.
    *
    * @return
    */
  def getVertices = {
    vertices
    /*val nodeIterator = graph.nodeIterator()

    val wikiIDs = new IntArrayList()
    for(i <- 0 until graph.numNodes()) {
      wikiIDs.add(getWikiID(nodeIterator.nextInt()))
    }

    wikiIDs.toIntArray()*/
  }


  override def copy(): ImmutableGraph = new WikiBVGraph(graph.copy(), wiki2node)

  override def numNodes(): Int = graph.numNodes()

  override def numArcs(): Long = graph.numArcs()

  override def randomAccess(): Boolean = graph.randomAccess()
}