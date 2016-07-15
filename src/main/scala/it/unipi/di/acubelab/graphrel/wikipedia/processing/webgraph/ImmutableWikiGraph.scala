package it.unipi.di.acubelab.graphrel.wikipedia.processing.webgraph

import it.unimi.dsi.fastutil.ints.{Int2IntArrayMap, Int2ObjectOpenHashMap, IntArrayList, IntOpenHashSet}
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

        if (directedEdges.containsKey(srcNodeID)) {
          directedEdges.get(srcNodeID).add(dst)
        } else {
          val srcList = new IntArrayList()
          srcList.add(dst)
          directedEdges.put(srcNodeID, srcList)
        }
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
    wiki2node.put(wikiID, nodeID)
    nodeID
  }

  override def outdegree(i: Int): Int = {
    outGraph.getOrDefault(i, new IntArrayList()).size
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
