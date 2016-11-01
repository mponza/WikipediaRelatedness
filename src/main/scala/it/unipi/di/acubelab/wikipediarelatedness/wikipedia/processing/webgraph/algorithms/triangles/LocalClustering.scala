package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.triangles

import it.unimi.dsi.fastutil.io.BinIO
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraphFactory

class LocalClustering {
  protected val coefficients = BinIO.loadFloats(Configuration.wikipedia("localClustering"))
  protected val wikiGraph = WikiGraphFactory.noLoopSymGraph


  def loadLocalClusteringCoefficients(path: String) : Array[Float] = BinIO.loadFloats(path)


  def getCoefficient(wikiID: Int) : Float = {
    val nodeID = wikiGraph.getNodeID(wikiID)
    coefficients(nodeID)
  }
}
