package it.unipi.di.acubelab.graphrel.wikipedia.processing.statistics

import java.io.{File, PrintWriter}

import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unipi.di.acubelab.graphrel.utils.{Configuration, WikiLinksReader}
import org.slf4j.LoggerFactory

import scala.io.Source

class WikiStats {
  val logger = LoggerFactory.getLogger(classOf[WikiStats])

  def processStatistics() = {
    generateNodesNumber
  }

  def generateNodesNumber() = {
    logger.info("Computing Wikipedia number of nodes...")

    val graphReader = new WikiLinksReader

    val nodes = new IntOpenHashSet()

    graphReader.foreach {
      case (src, dst) => nodes.add(src); nodes.add(dst)
    }

    // Writes total number of nodes.
    val n = nodes.size
    new File(Configuration.wikipedia.statistics.nNodes).getParentFile().mkdirs
    new PrintWriter(Configuration.wikipedia.statistics.nNodes) { write(n.toString); close }

    logger.info("Wikipedia Nodes detected: %d".format(n))
  }
}

object WikiStats {
  lazy val nNodes = Source.fromFile(Configuration.wikipedia.statistics.nNodes).getLines.mkString.toInt
}