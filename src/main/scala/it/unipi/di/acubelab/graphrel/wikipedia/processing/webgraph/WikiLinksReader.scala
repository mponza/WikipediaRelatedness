package it.unipi.di.acubelab.graphrel.wikipedia.processing.webgraph

import java.io.{BufferedReader, File, FileInputStream, InputStreamReader}
import java.util.zip.GZIPInputStream

import it.unipi.di.acubelab.graphrel.utils.Configuration

import scala.io.Source

/**
  * Reads wiki-links-sorted.gz file and allows to iterate over the pair of edges.
  */
class WikiLinksReader extends Traversable[(Int, Int)] {
  // Warning: The Wikipedia file MUST to be sorted on edges.

  val url = Configuration.wikipedia.wikiLinks

  val fileStream = Source.fromInputStream(
    new GZIPInputStream(
      new FileInputStream(
        new File(url.getPath)
      )
    )
  )

  /**
    * Warning: Applies f only on the unique edges of the Wikipedia graph.
    * @param f
    * @tparam U
    */
  def foreach[U](f: ((Int, Int)) => U) {
    var lastEdge = (-1, -1)

    for (line <- fileStream.getLines()) {
      val splitLine = line.split("\t")

      val src = splitLine(0).toInt
      val dst = splitLine(1).toInt

      if (lastEdge != (src, dst)) {
        f((src, dst))
      }

      lastEdge = (src, dst)
    }
  }

}
