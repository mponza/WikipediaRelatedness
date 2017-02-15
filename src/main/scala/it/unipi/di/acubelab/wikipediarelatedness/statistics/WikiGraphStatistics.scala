package it.unipi.di.acubelab.wikipediarelatedness.statistics

import it.unimi.dsi.webgraph.algo.{ConnectedComponents, NeighbourhoodFunction, SumSweepUndirectedDiameterRadius}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory


/**
  * Statistics over the Wikipedia graph.
  */
class WikiGraphStatistics {
  protected val logger = LoggerFactory.getLogger("Statistics")

  def compute() {

    val graph = WikiBVGraphFactory.make("sym")

    val n = NeighbourhoodFunction.compute(graph.graph, 24, null)
    logger.info("Average distance %1.2f".format( NeighbourhoodFunction.averageDistance(n) ))

    logger.info("NumNodes %d".format(graph.numNodes()))
    logger.info("Sym NumArcs %d".format(graph.numNodes()))
    logger.info("Directed NumArcs %d".format(WikiBVGraphFactory.make("out").numArcs()))

    val cc = ConnectedComponents.compute(graph.graph, 24, null)
    logger.info("Number of CC: %d".format(cc.numberOfComponents ))

    val lcc = ConnectedComponents.getLargestComponent(graph.graph, 24, null)
    logger.info("Number of Largest CC. NumNodes: %d.".format( lcc.numNodes()))
    
    val diameter = new SumSweepUndirectedDiameterRadius( graph.graph , SumSweepUndirectedDiameterRadius.OutputLevel.DIAMETER, null )
    diameter.compute()
    logger.info("Diameter: %d".format( diameter.getDiameter ))

  }
}
