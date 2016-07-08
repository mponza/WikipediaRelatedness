package it.unipi.di.acubelab.graphrel.wikipedia

import java.io.{File, FileInputStream}
import java.net.URL
import java.util.zip.GZIPInputStream

import it.unimi.dsi.fastutil.ints.{Int2ObjectOpenHashMap, IntArrayList}
import it.unimi.dsi.webgraph.{BVGraph, ImmutableGraph}
import it.unipi.di.acubelab.graphrel.utils.Configuration

import scala.io.Source


class WikiLinksReader(url: URL) extends Traversable[(Int, Int)] {

  val fileStream = Source.fromInputStream(
    new GZIPInputStream(
      new FileInputStream(
        new File(url.getPath)
      )
    )
  )

  def foreach[U](f: ((Int, Int)) => U) {
    for (line <- fileStream.getLines()) {
      val splitLine = line.split("\t")

      val src = splitLine(0).toInt
      val  dst = splitLine(1).toInt

      f((src, dst))
    }
  }
}


class ImmutableWikiGraph extends ImmutableGraph {
  val url = Configuration.wikipedia.graph
  val outEdges = loadWikipediaGraph(url)

  def loadWikipediaGraph(url: URL) : Int2ObjectOpenHashMap[IntArrayList] = {
    val directedEdges = new Int2ObjectOpenHashMap[IntArrayList]
    val graphReader = new WikiLinksReader(url)

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



class WikiGraph {
  val immutableGraph = new ImmutableWikiGraph()
  ///val graph = new BVGraph()
}
