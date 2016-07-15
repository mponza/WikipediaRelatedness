package it.unipi.di.acubelab.graphrel.utils

import java.nio.file.Paths

/**
  * TODO: create reference.conf file in /resources directory
  */
object Configuration {

  val wat = "http://wat.mkapp.it/wikidocs?lang=en"

  val projDir = System.getProperty("user.dir")

  val dataset = new {
    val wikiSim = getClass.getResource("/dataset/wikiSim411.csv").getPath
    val procWikiSim = Paths.get(projDir, "/data/dataset/wikiSim411.csv").toString
  }

  val wikipedia = new {
    // Read-only resources.
    val wikiLinks = getClass.getResource("/wikipedia/wiki-links-sorted.gz")

    // Generated resources.
    val wiki2node = Paths.get(projDir, "/data/processing/wikipedia/mapping/wiki2node").toString
    val outBVGraph = Paths.get(projDir, "/data/processing/wikipedia/out-bv-graph/out-wiki-links").toString
    val inBVGraph = Paths.get(projDir, "/data/processing/wikipedia/in-bv-graph/in-wiki-links").toString
    val symBVGraph = Paths.get(projDir, "/data/processing/wikipedia/sym-bv-graph/sym-wiki-links").toString
    val noLoopSymBVGraph = Paths.get(projDir, "/data/processing/wikipedia/no-loop-sym-bv-graph/no-loop-sym-wiki-links").toString

    val llp = Paths.get(projDir, "/data/processing/wikipedia/llp").toString


    val statistics = new {
      val nNodes = Paths.get(projDir, "/data/processing/wikipedia/statistics/nodes.txt").toString
    }
  }

  val benchmark =  Paths.get(projDir, "/data/benchmark").toString
}
