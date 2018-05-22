package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph

import java.io.{File, FileInputStream}
import java.util.concurrent.TimeUnit

import it.unimi.dsi.webgraph._
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.logging.ProgressLogger
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import org.slf4j.LoggerFactory


/**
  * Main class used to process the Wikipedia graph with WebGraph.
  *
  */
object WebGraphProcessor {
  val logger = LoggerFactory.getLogger("WebGraphProcessor")


  /**
    * Builds and stores a BVGraph from the raw Wikipedia Graph.
    *
    */
  def main(args: Array[String]) {
    logger.info("Building Wikipedia ImmutableGraph...")

    val outGraph = new ImmutableWikiGraph

    logger.info("Storing Wikipedia2BVGraph mapping...")
    storeMapping(outGraph.wiki2node, Config.getString("wikipedia.webgraph.mapping"))

    logger.info("Storing Out Wikipedia BVGraph...")
    storeBVGraph(outGraph, Config.getString("wikipedia.webgraph.out"))

    logger.info("Storing In Wikipedia BVGraph...")
    storeBVGraph(Transform.transpose(outGraph), Config.getString("wikipedia.webgraph.in"))

    logger.info("Storing Sym Wikipedia BVGraph...")
    storeBVGraph(Transform.symmetrize(outGraph), Config.getString("wikipedia.webgraph.sym"))

    logger.info("Wikipedia has been processed as BVGraph!")
  }


  /**
    * Saves graph to path as BVGraph.
    *
    * @param graph
    * @param path
    */
  def storeBVGraph(graph: ImmutableGraph, path: String) = {
    new File(path).getParentFile.mkdirs
    BVGraph.store(graph, path)
    logger.info("BVGraph stored in %s.".format(path))
  }


  def storeEFInGraph(graph: ImmutableGraph, path: String) = {
    new File(path).getParentFile.mkdirs
    EFGraph.store(graph, path)
    logger.info("EFGraph stored in %s.".format(path))
  }


  /**
    * Serializes the mapping between Wikipedia ID and its nodeID into path.
    *
    * @param wiki2node
    * @param path
    */
  def storeMapping(wiki2node: Int2IntOpenHashMap, path: String) = {
    new File(path).getParentFile.mkdirs
    BinIO.storeObject(wiki2node, path)

    val m = BinIO.loadObject(path).asInstanceOf[Int2IntOpenHashMap]
  }

}
