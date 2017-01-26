package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.triangles

import java.io.File
import java.nio.file.Paths

import es.yrbcn.graph.triangles.MainmemBitbasedTrianglesAlgorithm
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.webgraph.ImmutableGraph
import it.unipi.di.acubelab.wikipediarelatedness.utils.OldConfiguration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory

/**
  * Local Clustering Computation via Approximated Triangle Counting.
  * For more info see "Efficient Semi-streaming Algorithms for Local
  * Triangle Counting in Massive Graphs" (Becchetti, Boldi, Castillo, Gionis; KDD'08)
  */
class LocalClusteringProcessing {
  val logger = LoggerFactory.getLogger(classOf[LocalClusteringProcessing])


  def generateClusteringCoefficients(path: String = OldConfiguration.wikipedia("localClustering")) = {
    val wikiGraph = WikiBVGraphFactory.symNoLoopWikiBVGraph

    val triangles = generateTriangles(wikiGraph.graph)
    val lcc = generateLocalClusteringCoefficients(wikiGraph.graph, triangles)
    saveCoefficients(lcc, path)
  }


  def generateTriangles(graph: ImmutableGraph) : Array[Float]= {

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


  def generateLocalClusteringCoefficients(graph: ImmutableGraph, triangles: Array[Float]) : Array[Float] = {
    logger.info("Normalizing triangle counts...")

    val lcc = Array.fill[Float](graph.numNodes())(0f)

    for(i <- 0 until graph.numNodes()) {
      val ki = graph.outdegree(i)
      lcc(i) = triangles(i) / ( ki * (ki - 1) )
    }

    lcc
  }


  def saveCoefficients(coefficients: Array[Float], pathDir: String) = {
    new File(pathDir).mkdirs
    val llcPath = Paths.get(pathDir, "coefficients.bin").toString

    logger.info("Saving Local Clustring Coefficients into %s...".format(llcPath))
    BinIO.storeFloats(coefficients, llcPath)
  }
}
