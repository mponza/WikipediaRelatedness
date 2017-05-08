package it.unipi.di.acubelab.wikipediarelatedness.statistics

import java.util.concurrent.TimeUnit

import it.unimi.dsi.fastutil.objects.AbstractObjectList
import it.unimi.dsi.logging.ProgressLogger
import it.unimi.dsi.stat.Jackknife
import it.unimi.dsi.webgraph.ImmutableGraph
import it.unimi.dsi.webgraph.algo._
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory

import scala.math.BigDecimal.RoundingMode


/**
  * Statistics over the Wikipedia graph.
  */
object WikiGraphStatistics {
  protected val logger = LoggerFactory.getLogger("WikiGraphStatistics")


  class SDCDF extends SampleDistanceCumulativeDistributionFunction {

    def sample(graph: ImmutableGraph, k: Int) : Array[Array[Int]] = SampleDistanceCumulativeDistributionFunction.sample(graph, k, 24)


    // Code adapted from https://github.com/lhelwerd/WebGraph/blob/master/src/it/unimi/dsi/webgraph/algo/SampleDistanceCumulativeDistributionFunction.java
    def statisticSampling(graph: ImmutableGraph) = {
      val sample = this.sample(graph, 1000)

      // Computing samples
      var l = 0
      for(s <- sample) { l = Math.max(l, s.length) }
      val samples = new AbstractObjectList[Array[Double]] {

        override def get(index: Int): Array[Double] = {
          val result = new Array[Double](l)
          val s = sample(index)
          val n = graph.numNodes()

          for( i <- 0 until l ) {
            result(i) = s( Math.min( i, s.length - 1 ) ) * n
          }

          result
        }

        override def size(): Int = sample.length
      }

      val avgDist = Jackknife.compute(samples, ApproximateNeighbourhoodFunctions.AVERAGE_DISTANCE )
      for(i <- 0 until avgDist.estimate.length) {
        println("Average Distance")
        println("%1.3f".format(avgDist.bigEstimate(i).setScale(30, RoundingMode.HALF_EVEN)))
        println("%1.3f".format(avgDist.standardError(i)))
        println("%1.3f".format(avgDist.standardError(i) / avgDist.estimate(i)))
      }

    }
  }


  def main(args: Array[String]) {
    val graph = WikiBVGraphFactory.make("un.sym")

    logger.info("NumNodes %d".format(graph.numNodes()))
    logger.info("Sym NumArcs %d".format(graph.numNodes()))
    // logger.info("Directed NumArcs %d".format(WikiBVGraphFactory.make("out").numArcs()))

    // Connected Components
    val cc = ConnectedComponents.compute(graph.graph, 24, null)
    logger.info("Number of CC: %d".format(cc.numberOfComponents ))

    val lcc = ConnectedComponents.getLargestComponent(graph.graph, 24, null)
    logger.info("Number of Largest CC. NumNodes: %d.".format( lcc.numNodes()))

    // Diameter
    //val diameter = new SumSweepUndirectedDiameterRadius( lcc , SumSweepUndirectedDiameterRadius.OutputLevel.DIAMETER, null )
    //diameter.compute()
    //logger.info("Diameter: %d".format( diameter.getDiameter ))


    val sampling = new SDCDF
    sampling.statisticSampling(lcc)

    // Distance
    //val n = NeighbourhoodFunction.compute(graph.graph, 24, null)
    //logger.info("Average distance %1.2f".format( NeighbourhoodFunction.averageDistance(n) ))

    // Degree
    //degrees(WikiBVGraphFactory.make("out").graph, WikiBVGraphFactory.make("in").graph)
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
