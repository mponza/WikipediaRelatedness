package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.graph

import java.io.{File, FileInputStream}
import java.util.zip.GZIPInputStream

import scala.io.{BufferedSource, Source}

class WikiGraphTSVReader(filename: String) extends Traversable[(Int, Int)] {

  val fileStream = getFileStream(filename)


  /**
    * Return BufferedSource for reading a given filename that can be either gz or tsv format.
    * @param filename
    * @return
    */
  def getFileStream(filename: String): BufferedSource = {
    if(filename.endsWith("gz")) {

        Source.fromInputStream( new GZIPInputStream(
          new FileInputStream(
            new File(filename)
          )
        )
      )

    } else {
      Source.fromInputStream( new FileInputStream( new File(filename)) )
    }
  }


  /**
    * Warning: Applies f only on the unique edges of the Wikipedia graph.
    *
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
