package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph

import it.unimi.dsi.fastutil.ints.{Int2IntOpenHashMap, IntArrayList}
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.webgraph.{BVGraph, LazyIntIterator}
import it.unimi.dsi.webgraph.ImmutableGraph
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import org.slf4j.LoggerFactory


/**
  * Wrapper of the Wikipedia graph managed as a BVGraph.
  * This class transparently manages the mapping between wikiID and nodeID.
  *
  */
class WikiBVGraph(val path: String) {
  val logger = LoggerFactory.getLogger(getClass)

  lazy val graph = loadImmutableGraph()

  // WikiID -> NodeID mapping.
  protected lazy val wiki2node = BinIO.loadObject(Config.getString("wikipedia.webgraph.mapping"))
                                    .asInstanceOf[Int2IntOpenHashMap]
  protected lazy val node2wiki = reverseWiki2Node()


  /**
    * Loads a graph previously stored as a BVGraph in path.
    * @return
    */
  def loadImmutableGraph(): ImmutableGraph = {
    logger.info("Loading BVGraph from %s".format(path))
    val graph = BVGraph.load(path)
    logger.info("BVGraph loaded. |Nodes| = %d and |Edges| = %d".format(graph.numNodes, graph.numArcs))

    graph
  }



  //
  // Neighborhood Operations (wikiID -> nodeIDs).


  /**
    * Returns the successors (nodeIDs) of wikiID.
    *
    * @param wikiID
    * @return
    */
  def successors(wikiID: Int): LazyIntIterator = graph.successors(getNodeID(wikiID))



  /**
    * Returns the successors array (nodeIDs) of wikiID.
    *
    * @param wikiID
    * @return
    */
  def successorArray(wikiID: Int): Array[Int] = graph.successorArray(getNodeID(wikiID))


  /**
    * The degree of wikiID in the graph.
    *
    * @param wikiID
    * @return
    */
  def outdegree(wikiID: Int): Int = graph.outdegree(getNodeID(wikiID))



  //
  // Neighborhood Operations (nodeID -> nodeIDs)


  /**
    *
    *
    * @param nodeID
    * @return
    */
  def nodeSuccessors(nodeID: Int): LazyIntIterator = graph.successors(nodeID)


  def nodeSuccessorArray(nodeID: Int): Array[Int] = graph.successorArray(nodeID)


  def nodeOutDegree(nodeID: Int): Int = graph.outdegree(nodeID)



  //
  // WikiID -> NodeID mapping operations


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



  //
  // WikiID -> WikiIDs operations


  /**
    * Returns the WikiIDs successors of wikiID.
    *
    * @param wikiID
    * @return
    */
  def wikiSuccessors(wikiID: Int) : Array[Int] = {
    successorArray(wikiID).map(getWikiID(_))
  }


  def getWikiIDs() = {
    val nodeIterator = graph.nodeIterator()

    val wikiIDs = new IntArrayList()
    for(i <- 0 until graph.numNodes()) {
      wikiIDs.add(getWikiID(nodeIterator.nextInt()))
    }

    wikiIDs.toIntArray()
  }
}