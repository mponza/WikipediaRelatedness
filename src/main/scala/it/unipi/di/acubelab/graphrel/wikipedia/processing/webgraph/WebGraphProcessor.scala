package it.unipi.di.acubelab.graphrel.wikipedia.processing.webgraph


import java.io.File

import it.unimi.dsi.fastutil.ints.{Int2IntOpenHashMap}
import it.unimi.dsi.fastutil.io.{BinIO}
import it.unimi.dsi.law.graph.LayeredLabelPropagation
import it.unimi.dsi.webgraph.{BVGraph, ImmutableGraph, Transform}
import it.unipi.di.acubelab.graphrel.utils.Configuration
import org.slf4j.LoggerFactory


class WebGraphProcessor {
  val logger = LoggerFactory.getLogger(classOf[WebGraphProcessor])

  /**
    * Builds and stores a BVGraph from the raw Wikipedia Graph.
    */
  def generateBVGraph = {
    logger.info("Building Wikipedia ImmutableGraph...")
    val outGraph = new ImmutableWikiGraph

    logger.info("Storing Wikipedia2BVGraph mapping...")
    storeMapping(outGraph.wiki2node, Configuration.wikipedia("wiki2node"))

    logger.info("Storing Out Wikipedia BVGraph...")
    storeBVGraph(outGraph, Configuration.wikipedia("outBVGraph"))

    logger.info("Storing In Wikipedia BVGraph...")
    storeBVGraph(Transform.transpose(outGraph), Configuration.wikipedia("inBVGraph"))

    logger.info("Storing Sym Wikipedia BVGraph...")
    storeBVGraph(Transform.symmetrize(outGraph), Configuration.wikipedia("symBVGraph"))

    // See http://law.di.unimi.it/software/law-docs/it/unimi/dsi/law/graph/LayeredLabelPropagation.html
    logger.info("Storing Sym and Self-loopless Wikipedia BVGraph...")
    val noLoopGraph = Transform.filterArcs(outGraph, Transform.NO_LOOPS)
    storeBVGraph(Transform.symmetrizeOffline(noLoopGraph, 20000000), Configuration.wikipedia("noLoopSymBVGraph"))

    logger.info("Wikipedia has been processed as BVGraph!")
  }

  /**
    * Save graph to path as BVGraph.
    * @param graph
    * @param path
    */
  def storeBVGraph(graph: ImmutableGraph, path: String) = {
    new File(path).getParentFile.mkdirs
    BVGraph.store(graph, path)
    logger.info("BVGraph stored in %s!".format(path))
  }

  def storeMapping(wiki2node: Int2IntOpenHashMap, path: String) = {
    new File(path).getParentFile.mkdirs
    BinIO.storeObject(wiki2node, path)

    val m = BinIO.loadObject(path).asInstanceOf[Int2IntOpenHashMap]
  }

  def processLLP() : Unit = {
    logger.info("Loading Sym & loopless Wikipedia graph...")
    val graph = BVGraph.load(Configuration.wikipedia("noLoopSymBVGraph"))
    val llp = new LLPProcessor(graph)
    llp.process()
  }
}
