package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.clustering

import it.unimi.dsi.fastutil.io.BinIO
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory

/**
  * Class for accessing to local clustering coefficients stored to file from LocalClusteringProcessing class.
  *
  */
class LocalClustering {
  protected val logger = LoggerFactory.getLogger(classOf[LocalClustering])

  protected val coefficients = loadLocalClusteringCoefficients()
  protected val wikiGraph = WikiBVGraphFactory.makeWikiBVGraph("sym_no_loop")


  /**
    * Loads local clustering coefficients from file.
    *
    * @return
    */
  protected def loadLocalClusteringCoefficients() : Array[Float] = {
    logger.info("Loading Local Clustering Coefficients...")
    val coefficients = BinIO.loadFloats(Config.getString("wikipedia.cache.local_clustering"))
    logger.info("Local Clustering Coefficients loaded!")

    coefficients
  }


  /**
    * Returns local clustering coefficient of wikiID.
    *
    * @param wikiID
    * @return
    */
  def getCoefficient(wikiID: Int) : Float = {
    val nodeID = wikiGraph.getNodeID(wikiID)
    val coeff = coefficients(nodeID)

    if (coeff.isNaN) return 0f

    coeff
  }
}
