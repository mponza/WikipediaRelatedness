package it.unipi.di.acubelab.graphrel.wikipedia.processing.graph

import java.io.{File, FileInputStream}
import java.util.zip.GZIPInputStream

import it.unipi.di.acubelab.graphrel.utils.Configuration

import scala.io.Source

/**
  * Reads wiki-links-sorted.gz file and allows to iterate over the pair of edges.
  */
class WikiLinksReader extends Traversable[(Int, Int)] {
  val url = Configuration.wikipedia.wikiLinks

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
