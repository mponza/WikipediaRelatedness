package it.unipi.di.acubelab.graphrel.wikipedia.processing.webgraph

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections
import it.unimi.dsi.fastutil.ints.{Int2IntArrayMap, Int2ObjectOpenHashMap, IntArrayList, IntSortedSet}
import it.unimi.dsi.webgraph.{ImmutableGraph, LazyIntIterator}
import it.unipi.di.acubelab.graphrel.utils.WikiLinksReader

/**
  * Wikipedia Immutable Graph used to create a BVGraph.
  */
class ImmutableWikiGraph extends ImmutableGraph {
  val (outGraph, wiki2node) = loadWikipediaGraph

  /**
    * @return Wikipedia Immutable graph and the mapping between wikiID and nodeID.
    */
  def loadWikipediaGraph : (Int2ObjectOpenHashMap[IntArrayList], Int2IntArrayMap) = {
    val directedEdges = new Int2ObjectOpenHashMap[IntArrayList]

    val wiki2node = new Int2IntArrayMap

    val graphReader = new WikiLinksReader
    graphReader.foreach {
      case (src, dst)  =>
        val srcNodeID = wiki2NodeMapping(src, wiki2node)
        val dstNodeID = wiki2NodeMapping(dst, wiki2node)

        directedEdges.putIfAbsent(srcNodeID, new IntArrayList)
        directedEdges.get(srcNodeID).add(dstNodeID)

        directedEdges.putIfAbsent(dstNodeID, new IntArrayList)
    }

    for(i <- 0 to directedEdges.size - 1) {
      java.util.Collections.sort(directedEdges.get(i))
    }

    (directedEdges, wiki2node)
  }

  /**
    * If  wikiID is not in wiki2node then it is inserted.
    * @param wikiID
    * @param wiki2node
    * @return The nodeID of wikiId in the BVGraph.
    */
  def wiki2NodeMapping(wikiID: Int, wiki2node: Int2IntArrayMap) : Int = {
    val nextNodeID = wiki2node.size
    val nodeID = wiki2node.getOrDefault(wikiID, nextNodeID)
    wiki2node.putIfAbsent(wikiID, nodeID)
    nodeID
  }

  override def outdegree(i: Int): Int = {
    val s = outGraph.get(i).size()
    println(outGraph.get(i).size())
    s
  }

  override def copy: ImmutableGraph = {
    this
  }

  override def numNodes: Int = {
    wiki2node.size
  }

  override def randomAccess: Boolean = {
    true
  }

  class LazyNodesIterator(nodes: IntArrayList) extends LazyIntIterator {
    val nodesIterator = if (nodes != null) nodes.listIterator(0) else null

    override def nextInt() : Int = {
      java.util.Collections.sort(nodes)
      if(nodesIterator != null && nodesIterator.hasNext) nodesIterator.nextInt else -1
    }
    override def skip(x: Int) : Int = {
      nodesIterator.skip(x)
    }
  }

  override def successors(x: Int) : LazyIntIterator = {
    new LazyNodesIterator(outGraph.get(x))
  }
}
