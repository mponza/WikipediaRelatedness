package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph

import java.io.File

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.webgraph.{BVGraph, ImmutableGraph, Transform}
import it.unipi.di.acubelab.wikipediarelatedness.utils.OldConfiguration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.oldllp.{LLPProcessor, LLPTask}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.multillp.MultiLLPProcessor
import org.slf4j.LoggerFactory


/**
  * Main class used to process the Wikipedia graph with WebGraph.
  *
  */
class WebGraphProcessor {
  val logger = LoggerFactory.getLogger(getClass)


  /**
    * Builds and stores a BVGraph from the raw Wikipedia Graph.
    *
    */
  def generateBVGraph = {
    logger.info("Building Wikipedia ImmutableGraph...")
    val outGraph = new ImmutableWikiGraph

    logger.info("Storing Wikipedia2BVGraph mapping...")
    storeMapping(outGraph.wiki2node, OldConfiguration.wikipedia("wiki2node"))

    logger.info("Storing Out Wikipedia BVGraph...")
    storeBVGraph(outGraph, OldConfiguration.wikipedia("outBVGraph"))

    logger.info("Storing In Wikipedia BVGraph...")
    storeBVGraph(Transform.transpose(outGraph), OldConfiguration.wikipedia("inBVGraph"))

    logger.info("Storing Sym Wikipedia BVGraph...")
    storeBVGraph(Transform.symmetrize(outGraph), OldConfiguration.wikipedia("symBVGraph"))

    // See http://law.di.unimi.it/software/law-docs/it/unimi/dsi/law/graph/LayeredLabelPropagation.html
    logger.info("Storing Sym and Self-loopless Wikipedia BVGraph...")
    val noLoopGraph = Transform.filterArcs(outGraph, Transform.NO_LOOPS)
    storeBVGraph(Transform.symmetrizeOffline(noLoopGraph, 20000000), OldConfiguration.wikipedia("noLoopSymBVGraph"))

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
    logger.info("BVGraph stored in %s!".format(path))
  }


  /**
    * Serializes the mapping between Wikipedia ID and its nodeID into path.
    * @param wiki2node
    * @param path
    */
  def storeMapping(wiki2node: Int2IntOpenHashMap, path: String) = {
    new File(path).getParentFile.mkdirs
    BinIO.storeObject(wiki2node, path)

    val m = BinIO.loadObject(path).asInstanceOf[Int2IntOpenHashMap]
  }

}
