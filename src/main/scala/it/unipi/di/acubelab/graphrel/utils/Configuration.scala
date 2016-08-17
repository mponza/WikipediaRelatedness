package it.unipi.di.acubelab.graphrel.utils

import java.nio.file.Paths

/**
  * TODO: create reference.conf file in /resources directory.
  *       0. Create a conf file with not absolute paths.
  *       1. Use ConfFactory and load conf file.
  *       2. Parse it and extend specific file with projDir paths.
  *
  */
object Configuration {

  val wat = "http://wat.mkapp.it/wikidocs?lang=en"
  val cosimrank = "http://localhost:5555"
  val subgraphThreshold = 4000

  val projDir = System.getProperty("user.dir")

  lazy val dataset = Map (
    "wikiSim" -> getClass.getResource("/dataset/wikiSim411.csv").getPath,
    "procWikiSim" -> Paths.get(projDir, "/data/dataset/wikiSim411.csv").toString
  )

  lazy val wikipedia = Map(
    "wikiLinks" -> getClass.getResource("/wikipedia/wiki-links-sorted.gz").getPath,

    "wiki2node" -> Paths.get(projDir, "/data/processing/wikipedia/mapping/wiki2node").toString,

    "outBVGraph" -> Paths.get(projDir, "/data/processing/wikipedia/out-bv-graph/out-wiki-links").toString,
    "inBVGraph" -> Paths.get(projDir, "/data/processing/wikipedia/in-bv-graph/in-wiki-links").toString,
    "symBVGraph" -> Paths.get(projDir, "/data/processing/wikipedia/sym-bv-graph/sym-wiki-links").toString,
    "noLoopSymBVGraph" -> Paths.get(projDir,
      "/data/processing/wikipedia/no-loop-sym-bv-graph/no-loop-sym-wiki-links").toString,   // no self loops

    "llp" -> Paths.get(projDir, "/data/processing/wikipedia/llp").toString,

    "multiLLP" -> Paths.get(projDir, "/data/processing/wikipedia/multiLLP").toString,

    "corpus" -> getClass.getResource("/w2v/wikipedia-w2v-linkCorpus.e0.100.tr.bin").getPath,
    "deepWalk" -> getClass.getResource("/w2v/wikipedia-w2v-deepWalk.e0.100.tr.bin").getPath,
    // Link Corpus and DeepWalk mixed.
    "deepCorpus" -> getClass.getResource("/w2v/wikipedia-w2v-deepWalkMixed.e0.100.tr.bin").getPath,
    "coOccurrence" -> getClass.getResource("/w2v/wikipedia-w2v-coOccurrence.e0.100.tr.bin").getPath,

    "langModel" -> getClass.getResource("/languageModel/wiki.binary").getPath
  )

  val benchmark =  Paths.get(projDir, "/data/benchmark").toString

  val analysis = Map(
    "correlation" -> Paths.get(projDir, "/data/analysis/correlation").toString,
    "classification" -> Paths.get(projDir, "/data/analysis/classification").toString
  )
}
