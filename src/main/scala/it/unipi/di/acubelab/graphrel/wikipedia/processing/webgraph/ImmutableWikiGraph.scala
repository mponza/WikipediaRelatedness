package it.unipi.di.acubelab.graphrel.wikipedia.processing.webgraph

import it.unimi.dsi.fastutil.ints.{Int2ObjectOpenHashMap, IntArrayList, IntOpenHashSet}
import it.unimi.dsi.webgraph.{ImmutableGraph, LazyIntIterator}

/**
  * Wikipedia Immutable Graph used to create a BVGraph.
  */
class ImmutableWikiGraph extends ImmutableGraph {
  val (outGraph, nNodes) = loadWikipediaGraph

  /**
    * TODO: Create mapping between wikiID and node index in the graph.
    * @return Wikipedia Immutable graph and the total numebr of nodes.
    */
  def loadWikipediaGraph : (Int2ObjectOpenHashMap[IntArrayList], Int) = {
    val directedEdges = new Int2ObjectOpenHashMap[IntArrayList]
    var maxID = -1
    val graphReader = new WikiLinksReader

    graphReader.foreach {
      case (src, dst)  =>
        if (directedEdges.containsKey(src)) {
          directedEdges.get(src).add(dst)
        } else {
          val srcList = new IntArrayList()
          srcList.add(dst)
          directedEdges.put(src, srcList)
        }

        maxID = Array(src, dst, maxID).reduceLeft(_ max _)
    }

    (directedEdges, maxID + 1)
  }

  override def outdegree(i: Int): Int = {
    outGraph.getOrDefault(i, new IntArrayList()).size
  }

  override def copy: ImmutableGraph = {
    this
  }

  override def numNodes: Int = {
    nNodes
  }

  override def randomAccess: Boolean = {
    true
  }

  class LazyNodesIterator(nodes: IntArrayList) extends LazyIntIterator {
    val nodesIterator = nodes.listIterator(0)

    override def nextInt() : Int = if (nodesIterator.hasNext) nodesIterator.nextInt else -1
    override def skip(x: Int) : Int = nodesIterator.skip(x)
  }

  override def successors(x: Int) : LazyIntIterator = {
    new LazyNodesIterator(outGraph.getOrDefault(x, new IntArrayList()))
  }
}
