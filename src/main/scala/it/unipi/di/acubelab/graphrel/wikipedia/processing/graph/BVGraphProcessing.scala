package it.unipi.di.acubelab.graphrel.wikipedia.processing.graph


import java.nio.file.Paths

import it.unimi.dsi.webgraph.BVGraph
import it.unipi.di.acubelab.graphrel.utils.Configuration
import org.slf4j.LoggerFactory


class BVGraphProcessing {
  val logger = LoggerFactory.getLogger(classOf[BVGraphProcessing])

  val bvWikiGraphPath = Paths.get(Configuration.wikipedia.directory.getPath,
    Configuration.CONSTS.bvWikiGraphName).toString

  /**
    * Builds and stores a BVGraph from the raw Wikipedia Graph.
    */
  def processWikiGraph = {
    logger.info("Building Wikipedia ImmutableGraph...")
    val immWikiGraph = new ImmutableWikiGraph  // fix me! I'm null!

    logger.info("Storing Wikipedia BVGraph...")

    println(immWikiGraph)
    //BVGraph.store(immWikiGraph, bvWikiGraphPath)

    logger.info("Wikipedia has been processed as BVGraph!")
  }

  /*def loadBVWikiGraph : BVGraph = {
    logger.info("Loading Wikipedia as BVGraph...")

    logger.info("Wikipedia has been loaded as BVGraph!")
  }*/
}
