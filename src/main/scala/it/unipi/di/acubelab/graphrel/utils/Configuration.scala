package it.unipi.di.acubelab.graphrel.utils

import java.nio.file.Paths

/**
  * TODO: create reference.conf file in /resources directory
  */
object Configuration {

  val projDir = System.getProperty("user.dir")

  val dataset = new {
    val wikiSim = getClass.getResource("/dataset/wikiSim411.csv")
  }

  val wikipedia = new {
    // Read-only resources.
    val wikiLinks = getClass.getResource("/wikipedia/wiki-links-sorted.gz")

    // Generated resources.
    val outBVGraph = Paths.get(projDir, "/data/processing/wikipedia/out-bv-graph/out-wiki-links").toString
    val inBVGraph = Paths.get(projDir, "/data/processing/wikipedia/in-bv-graph/in-wiki-links").toString
    val symBVGraph = Paths.get(projDir, "/data/processing/wikipedia/sym-bv-graph/sym-wiki-links").toString
    val noLoopSymBVGraph = Paths.get(projDir, "/data/processing/wikipedia/no-loop-sym-bv-graph/no-loop-sym-wiki-links").toString

    val llp = Paths.get(projDir, "/data/processing/wikipedia/llp").toString
  }
}
