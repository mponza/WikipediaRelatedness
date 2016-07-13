package it.unipi.di.acubelab.graphrel.wikipedia.processing.statistics

import java.io.PrintWriter

import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unipi.di.acubelab.graphrel.utils.{Configuration, WikiLinksReader}

import scala.io.Source

class WikiStats {

  def processStatistics() = {
    generateNodesNumber
  }

  def generateNodesNumber() = {
    val graphReader = new WikiLinksReader

    val nodes = new IntOpenHashSet()

    graphReader.foreach {
      case (src, dst) => nodes.add(src); nodes.add(dst)
    }
    val n = nodes.size

    val writer = new PrintWriter(Configuration.wikipedia.statistics.nNodes)
    writer.write(n)
    writer.close
  }
}

object WikiStats {
  lazy val nNodes = Source.fromFile(Configuration.wikipedia.statistics.nNodes).getLines.mkString.toInt
}