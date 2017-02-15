package it.unipi.di.acubelab.wikipediarelatedness.statistics

import java.util.concurrent.TimeUnit

import it.unimi.dsi.logging.ProgressLogger
import it.unimi.dsi.webgraph.{BVGraph, ImmutableGraph}
import it.unimi.dsi.webgraph.algo.{ConnectedComponents, NeighbourhoodFunction, SumSweepUndirectedDiameterRadius}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory


/**
  * Statistics over the Wikipedia graph.
  */
object WikiGraphStatistics {
  protected val logger = LoggerFactory.getLogger("WikiGraphStatistics")

  def main() {
    val graph = WikiBVGraphFactory.make("sym")

    logger.info("NumNodes %d".format(graph.numNodes()))
    logger.info("Sym NumArcs %d".format(graph.numNodes()))
    // logger.info("Directed NumArcs %d".format(WikiBVGraphFactory.make("out").numArcs()))

    // Connected Components
    val cc = ConnectedComponents.compute(graph.graph, 24, null)
    logger.info("Number of CC: %d".format(cc.numberOfComponents ))

    val lcc = ConnectedComponents.getLargestComponent(graph.graph, 24, null)
    logger.info("Number of Largest CC. NumNodes: %d.".format( lcc.numNodes()))

    // Diameter
    val diameter = new SumSweepUndirectedDiameterRadius( graph.graph , SumSweepUndirectedDiameterRadius.OutputLevel.DIAMETER, null )
    diameter.compute()
    logger.info("Diameter: %d".format( diameter.getDiameter ))

    // Distance
    val n = NeighbourhoodFunction.compute(graph.graph, 24, null)
    logger.info("Average distance %1.2f".format( NeighbourhoodFunction.averageDistance(n) ))

    // Degree
    degrees(WikiBVGraphFactory.make("out").graph, WikiBVGraphFactory.make("in").graph)
  }


  protected def degrees(outGraph: ImmutableGraph, inGraph: ImmutableGraph) = {
    var (outmin, outmax, outsum) = (Int.MaxValue, 0, 0)
    var (inmin, inmax, insum) = (Int.MaxValue, 0, 0)

    val pl = new ProgressLogger(logger, 1, TimeUnit.MINUTES)
    pl.start("Starting computing degree statistics...")
    for(node <- 1 until outGraph.numNodes()) {

      val outdegree = outGraph.outdegree(node)
      outmin = Math.min( outmin, outdegree )
      outmax = Math.max( outmax, outdegree )
      outsum += outdegree

      val indegree = inGraph.outdegree(node)
      inmin = Math.min( inmin, indegree )
      inmax = Math.max( inmax, indegree )
      insum += indegree

      pl.update()
    }
    pl.done()


    logger.info("Out-statistics: min %d, max %d, avg %1.2f".format( outmin, outmax, outsum.toFloat / outGraph.numNodes() ))
    logger.info("In-statistics: min %d, max %d, avg %1.2f".format( inmin, inmax, insum.toFloat / outGraph.numNodes() ))
  }
}
