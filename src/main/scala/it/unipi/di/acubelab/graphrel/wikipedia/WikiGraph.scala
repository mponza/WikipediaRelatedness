package it.unipi.di.acubelab.graphrel.wikipedia

import java.io.File
import java.net.URL

import com.github.tototoshi.csv.CSVReader
import it.unimi.dsi.fastutil.ints.{Int2ObjectOpenHashMap, IntArrayList}
import it.unimi.dsi.webgraph.{BVGraph, ImmutableGraph}


class ImmutableWikiGraph extends ImmutableGraph {
  val url = getClass.getResource("/wikiGraph.csv")
  val outEdges = loadWikipediaGraph(url)

  def loadWikipediaGraph(url: URL) : Int2ObjectOpenHashMap[IntArrayList] = {
    val directedEdges = new Int2ObjectOpenHashMap[IntArrayList]
    val csvReader = CSVReader.open(new File(url.getPath))

    csvReader.foreach {
      fields =>
        val src = fields(0).toInt
        val dst = fields(1).toInt

        val srcList = if (directedEdges.containsKey(src)) directedEdges.get(src) else new IntArrayList()
        srcList.add(dst)
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



class WikiGraph {
  val immutableGraph = new ImmutableWikiGraph()
  ///val graph = new BVGraph()
}
