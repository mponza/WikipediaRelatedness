package it.unipi.di.acubelab.graphrel.wikipedia.processing.graph

import it.unimi.dsi.fastutil.ints.{Int2ObjectOpenHashMap, IntArrayList}
import it.unimi.dsi.webgraph.ImmutableGraph

/**
  * Wikipedia Immutable Graph used to create a BVGraph.
  */
class ImmutableWikiGraph extends ImmutableGraph {
  val outEdges = loadWikipediaGraph

  def loadWikipediaGraph : Int2ObjectOpenHashMap[IntArrayList] = {
    val directedEdges = new Int2ObjectOpenHashMap[IntArrayList]
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
    }

    directedEdges
  }

  override def outdegree(i: Int): Int = {
    return outEdges.get(i).size
  }

  override def copy(): ImmutableGraph = {
    this
  }

  override def numNodes(): Int = {
    return outEdges.size
  }

  override def randomAccess(): Boolean = {
    true
  }
}
