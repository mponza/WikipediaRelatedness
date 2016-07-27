package it.unipi.di.acubelab.graphrel.utils

import java.nio.file.Paths

/**
  * TODO: create reference.conf file in /resources directory
  */
object Configuration {

  val wat = "http://wat.mkapp.it/wikidocs?lang=en"

  val projDir = System.getProperty("user.dir")

  val dataset = Map (
    "wikiSim" -> getClass.getResource("/dataset/wikiSim411.csv").getPath,
    "procWikiSim" -> Paths.get(projDir, "/data/dataset/wikiSim411.csv").toString
  )

  val wikipedia = Map(
    "wikiLinks" -> getClass.getResource("/wikipedia/wiki-links-sorted.gz").getPath,

    "wiki2node" -> Paths.get(projDir, "/data/processing/wikipedia/mapping/wiki2node").toString,

    "outBVGraph" -> Paths.get(projDir, "/data/processing/wikipedia/out-bv-graph/out-wiki-links").toString,
    "inBVGraph" -> Paths.get(projDir, "/data/processing/wikipedia/in-bv-graph/in-wiki-links").toString,
    "symBVGraph" -> Paths.get(projDir, "/data/processing/wikipedia/sym-bv-graph/sym-wiki-links").toString,
    "noLoopSymBVGraph" -> Paths.get(projDir, "/data/processing/wikipedia/no-loop-sym-bv-graph/no-loop-sym-wiki-links").toString,

    "llp" -> Paths.get(projDir, "/data/processing/wikipedia/llp").toString,

    "multiLLP" -> Paths.get(projDir, "/data/processing/wikipedia/robustLLP").toString,

    "corpus" -> Paths.get(projDir, "/data/w2v/wikipedia-w2v-linkCorpus.w2v").toString,
    "deepWalk" -> Paths.get(projDir, "/data/w2v/wikipedia-w2v-deepWalk.w2v").toString,
    "deepCorpus" -> Paths.get(projDir, "/data/w2v/wikipedia-w2v-deepWalkMixed.w2v").toString, // Link Corpus and DeepWalk mixed
    "coOccurrence" -> Paths.get(projDir, "/data/w2v/wikipedia-w2v-coOccurrence.w2v").toString
  )

  val benchmark =  Paths.get(projDir, "/data/benchmark").toString
}
