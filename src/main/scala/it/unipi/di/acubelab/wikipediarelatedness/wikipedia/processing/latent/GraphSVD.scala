package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.latent

import java.io.{File, FileInputStream}
import java.util.zip.GZIPInputStream

import it.unimi.dsi.fastutil.floats.FloatArrayList
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unipi.di.acubelab.wikipediarelatedness.utils.OldConfiguration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.{WikiBVGraph, WikiGraph$, WikiBVGraphFactory}
import org.slf4j.LoggerFactory

import scala.io.Source

class GraphSVD(path : String = OldConfiguration.graphSVD("left"), embeddingSize: Int = 100) {
  val logger = LoggerFactory.getLogger(classOf[GraphSVD])

  protected lazy val eigenVectors = loadEigenVectors(path)
  val wikiGraph = WikiBVGraphFactory.outWikiBVGraph

  /**
    * Loads eigenvectors from path where each row is a eigenvector of ~4M of doubles.
    *
    * @return [i-th_eigenVector: eigenVector] (namely 0-th is the highest, 1-th the second highest, ...)
    */
  def loadEigenVectors(path: String) : ObjectArrayList[FloatArrayList] = {
    // Each row of path is an eigenvector.
    val eigenVectors = new ObjectArrayList[FloatArrayList]

    val reader = Source.fromInputStream(
      new GZIPInputStream(
        new FileInputStream(
          new File(path)
        )
      )
    )

    logger.info("Loading eigenvectors from %s...".format(path))
    for ((line, index) <- reader.getLines().zipWithIndex) {
      val values = line.split("\t").map(_.toFloat)
      eigenVectors.add(new FloatArrayList(values))
    }

    eigenVectors
  }

  /**
    * Maps wikiId to the corresponding latent space.
    *
    * @return Vector made by all wikiID-th compontents of the first embeddingSize eigenvectors.
    */
  def embeddingVector(wikiID: Int) : FloatArrayList = {
    val wikiIndex = wikiGraph.getNodeID(wikiID)
    val eigenVector =  eigenVectors.get(wikiIndex)

    val size = if(embeddingSize <= 0) eigenVector.size else embeddingSize

    new FloatArrayList(eigenVector.toFloatArray, 0, size)
  }
}
