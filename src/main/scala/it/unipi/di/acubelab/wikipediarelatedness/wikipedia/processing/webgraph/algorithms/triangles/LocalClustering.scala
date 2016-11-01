package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.triangles

import java.nio.file.Paths

import it.unimi.dsi.fastutil.io.BinIO
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraphFactory
import org.slf4j.LoggerFactory

class LocalClustering {
  val logger = LoggerFactory.getLogger(classOf[LocalClustering])

  protected val coefficients = loadLocalClusteringCoefficients()
  protected val wikiGraph = WikiGraphFactory.noLoopSymGraph


  def loadLocalClusteringCoefficients() : Array[Float] = {
    val path = Paths.get(Configuration.wikipedia("localClustering"), "coefficients.bin").toString

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
