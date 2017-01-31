package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.clustering

import java.io.File

import es.yrbcn.graph.triangles.MainmemBitbasedTrianglesAlgorithm
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.webgraph.ImmutableGraph
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory


/**
  * Local Clustering Computation via Approximated Triangle Counting.
  * For more info see "Efficient Semi-streaming Algorithms for Local
  * Triangle Counting in Massive Graphs" (Becchetti, Boldi, Castillo, Gionis; KDD'08)
  *
  */
class LocalClusteringProcessing {
  protected val logger = LoggerFactory.getLogger(getClass)


  /**
    * Generated approximated local clustering coefficients and save them into file.
    *
    */
  def generateClusteringCoefficients() = {
    val wikiGraph = WikiBVGraphFactory.make("sym_no_loop")

    val triangles = generateTriangles(wikiGraph.graph)
    val lcc = generateLocalClusteringCoefficients(wikiGraph.graph, triangles)
    saveCoefficients(lcc, Config.getString("wikipedia.cache.local_clustering"))
  }


  /**
    *
    *
    * @param graph
    * @return
    */
  protected def generateTriangles(graph: ImmutableGraph) : Array[Float]= {

    // maxDistance seems not to be used by the algorithm
    val algorithm = new MainmemBitbasedTrianglesAlgorithm(graph, 0, 1)
    algorithm.setMaxPasses(100)
    algorithm.init()

    logger.info("Running Main Memory Bit-based Triangle Algorithm...")
    while(!algorithm.done()) {
      algorithm.step()
    }

    logger.info("Counting triangles...")
    algorithm.countTriangles()

    algorithm.triangles.map(_.toFloat)
  }


  /**
    *
    *
    * @param graph
    * @param triangles
    * @return
    */
  protected def generateLocalClusteringCoefficients(graph: ImmutableGraph, triangles: Array[Float]) : Array[Float] = {
    logger.info("Normalizing triangle counts...")

    val lcc = Array.fill[Float](graph.numNodes())(0f)

    for(i <- 0 until graph.numNodes()) {
      val ki = graph.outdegree(i)
      lcc(i) = triangles(i) / ( ki * (ki - 1) )
    }

    lcc
  }


  /**
    * Store coefficients into path
    *
    * @param coefficients
    * @param path
    */
  protected def saveCoefficients(coefficients: Array[Float], path: String) = {
    new File(new File(path).getParent).mkdirs

    logger.info("Saving Local Clustring Coefficients into %s...".format(path))
    BinIO.storeFloats(coefficients, path)
    logger.info("Local Clustring Coefficients correctly stored!")
  }
}
