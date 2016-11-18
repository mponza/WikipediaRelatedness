package it.unipi.di.acubelab.wikipediarelatedness.utils

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
  val cosimrank = "http://127.0.0.1:9080"
  val subgraphThreshold = 4000

  val projDir = System.getProperty("user.dir")

  lazy val dataset = Map (
    "wikiSim" -> getClass.getResource("/dataset/wikiSim411.csv").getPath,
    "procWikiSim" -> Paths.get(projDir, "/data/dataset/wikiSim411.csv").toString
  )

  lazy val nyt = Map(
    "ss" -> Paths.get(projDir, "/data/dataset/nyt_wiki_pairs/ss.csv").toString,
    "ns" -> Paths.get(projDir, "/data/dataset/nyt_wiki_pairs/ns.csv").toString,
    "nn" -> Paths.get(projDir, "/data/dataset/nyt_wiki_pairs/nn.csv").toString
  )

  lazy val nyt_enhanced = Map(
    "ss" -> Paths.get(projDir, "/data/dataset/nyt_wiki_pairs/enhanced/ss.csv").toString,
    "ns" -> Paths.get(projDir, "/data/dataset/nyt_wiki_pairs/enhanced/ns.csv").toString,
    "nn" -> Paths.get(projDir, "/data/dataset/nyt_wiki_pairs/enhanced/nn.csv").toString
  )

  lazy val ibmDir = getClass.getResource("/ibm").getPath

  lazy val wikipedia = Map(
    "wikiLinks" -> getClass.getResource("/wikipedia/wiki-links-sorted.gz").getPath,
    "linkCorpus" -> getClass.getResource("/wikipedia/wikipedia-w2v-linkCorpus.json.gz").getPath,

    "distances" -> Paths.get(projDir, "/data/processing/wikipedia/distances").toString,

    "lucene" -> Paths.get(projDir, "/data/processing/wikipedia/lucene").toString,

    "wiki2node" -> Paths.get(projDir, "/data/processing/wikipedia/mapping/wiki2node").toString,

    "outBVGraph" -> Paths.get(projDir, "/data/processing/wikipedia/out-bv-graph/out-wiki-links").toString,
    "inBVGraph" -> Paths.get(projDir, "/data/processing/wikipedia/in-bv-graph/in-wiki-links").toString,
    "symBVGraph" -> Paths.get(projDir, "/data/processing/wikipedia/sym-bv-graph/sym-wiki-links").toString,
    "noLoopSymBVGraph" -> Paths.get(projDir,
      "/data/processing/wikipedia/no-loop-sym-bv-graph/no-loop-sym-wiki-links").toString,   // no self loops

    "symDistances" -> Paths.get(projDir, "/data/processing/wikipedia/distances/symDistance.bin").toString,

    "llp" -> Paths.get(projDir, "/data/processing/wikipedia/llp").toString,
    "multiLLP" -> Paths.get(projDir, "/data/processing/wikipedia/multiLLP").toString,

    "corpus" -> getClass.getResource("/w2v/wikipedia-w2v-linkCorpus.e0.100.tr.bin").getPath,
    "deepWalk" -> getClass.getResource("/w2v/wikipedia-w2v-deepWalk.e0.100.tr.bin").getPath,

    "line" -> Paths.get(projDir, "/data/processing/wikipedia/line").toString,

    "localClustering" -> Paths.get(projDir, "/data/processing/wikipedia/localClustering").toString,

    "esaCache" -> Paths.get(projDir, "/data/processing/wikipedia/ESACache").toString,

    "instance-types" -> getClass.getResource("/wikipedia/enwiki-20160305-instance-types-transitive.ttl.gz").getPath,
    "title-id" -> getClass.getResource("/wikipedia/freebase-mapping.tsv").getPath

  /*
    // Link Corpus and DeepWalk mixed.
    "deepCorpus" -> getClass.getResource("/w2v/wikipedia-w2v-deepWalkMixed.e0.100.tr.bin").getPath,
    "coOccurrence" -> getClass.getResource("/w2v/wikipedia-w2v-coOccurrence.e0.100.tr.bin").getPath,

    "el-1st-dw" -> getClass.getResource("/w2v/wikipedia-w2v-el-1st-deepWalk.w2v.bin").getPath,
    "el-1st" -> getClass.getResource("/w2v/wikipedia-w2v-el-1st.w2v.bin").getPath,
    "el-dw" -> getClass.getResource("/w2v/wikipedia-w2v-el-deepWalk.w2v.bin").getPath,

    "dw10" -> getClass.getResource("/w2v/wikipedia-w2v-el-deepWalk-0.10.w2v.bin").getPath,
    "dw30" -> getClass.getResource("/w2v/wikipedia-w2v-el-deepWalk-0.30.w2v.bin").getPath,
    "dw50" -> getClass.getResource("/w2v/wikipedia-w2v-el-deepWalk-0.50.w2v.bin").getPath,
    "dw70" -> getClass.getResource("/w2v/wikipedia-w2v-el-deepWalk-0.70.w2v.bin").getPath,
    "dw90" -> getClass.getResource("/w2v/wikipedia-w2v-el-deepWalk-0.90.w2v.bin").getPath,

    "dwsg" -> getClass.getResource("/w2v/wikipedia-w2v-sg-el-deepWalk.w2v.bin").getPath,
    "sg" -> getClass.getResource("/w2v/wikipedia-w2v-sg-et.w2v.bin").getPath,


    "langModel" -> getClass.getResource("/languageModel/wiki.binary").getPath*/
  )

  val benchmark =  Paths.get(projDir, "/data/benchmark").toString
  val classification = Paths.get(projDir, "/data/classification").toString

  val analysis = Map(
    "correlation" -> Paths.get(projDir, "/data/analysis/correlation").toString,
    "classification" -> Paths.get(projDir, "/data/analysis/classification").toString
  )

  val graphSVD = Map(
    "left" -> Paths.get(projDir, "/data/processing/wikipedia/latent/svd/eigen_left.csv.gz").toString,
    "right" -> Paths.get(projDir, "/data/processing/wikipedia/latent/svd/eigen_right.csv.gz").toString
  )

  val lda = Map(
    "wiki_lda" -> Paths.get(projDir, "/data/processing/wikipedia/latent/gensim/wiki_lda/lda_wiki_docs.gz").toString
  )

  val intervals = Map("white" -> (0.0, 0.4), "gray" -> (0.4, 0.7), "black" -> (0.7, 1.0))
}
