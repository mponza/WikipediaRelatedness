package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.triangles

import java.nio.file.Paths

import it.unimi.dsi.fastutil.io.BinIO
import it.unipi.di.acubelab.wikipediarelatedness.utils.OldConfiguration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory

class LocalClustering {
  val logger = LoggerFactory.getLogger(classOf[LocalClustering])

  protected val coefficients = loadLocalClusteringCoefficients()
  protected val wikiGraph = WikiBVGraphFactory.symNoLoopWikiBVGraph


  def loadLocalClusteringCoefficients() : Array[Float] = {
    val path = Paths.get(OldConfiguration.wikipedia("localClustering"), "coefficients.bin").toString

    logger.info("Loading Local Clustering Coefficients...")
    BinIO.loadFloats(path)
  }


  def getCoefficient(wikiID: Int) : Float = {
    val nodeID = wikiGraph.getNodeID(wikiID)
    val coeff = coefficients(nodeID)

    if (coeff.isNaN) return 0f

    coeff
  }
}
