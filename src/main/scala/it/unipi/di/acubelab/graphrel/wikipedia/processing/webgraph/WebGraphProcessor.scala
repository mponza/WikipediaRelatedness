package it.unipi.di.acubelab.graphrel.wikipedia.processing.webgraph


import java.io.File

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

    logger.info("Storing Out Wikipedia BVGraph...")
    storeBVGraph(outGraph, Configuration.wikipedia.outBVGraph)

    logger.info("Storing In Wikipedia BVGraph...")
    storeBVGraph(Transform.transpose(outGraph), Configuration.wikipedia.inBVGraph)

    logger.info("Storing Sym Wikipedia BVGraph...")
    storeBVGraph(Transform.symmetrize(outGraph), Configuration.wikipedia.symBVGraph)

    logger.info("Wikipedia has been processed as BVGraph!")
  }

  /**
    * Save graph to path as BVGraph.
    * @param graph
    * @param path
    */
  def storeBVGraph(graph: ImmutableGraph, path: String) = {
    new File(path).getParentFile().mkdirs
    BVGraph.store(graph, path)
    logger.info("BVGraph stored in %s!".format(path))
  }

  def processLLP = {
    val bvGraph = BVGraph.load(Configuration.wikipedia.outBVGraph)

    //val llp = new LayeredLabelPropagation()
  }
}
