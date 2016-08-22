package it.unipi.di.acubelab.wikipediarelatedness.latent

import java.io.{File, FileInputStream}
import java.util.zip.GZIPInputStream

import it.unimi.dsi.fastutil.doubles.DoubleArrayList
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.WikiBVGraph
import org.slf4j.LoggerFactory

import scala.io.Source

class GraphSVD(path : String = Configuration.eigen("left")) {
  val logger = LoggerFactory.getLogger(classOf[GraphSVD])
  lazy val eigenVectors = loadEigenVectors(path)

  /**
    * Loads eigenvectors from path where each row is a eigenvector of ~4M of doubles.
    * @return {i-th_eigenVector: eigenVector} (0-th is the highest, 1-th the second highest, ...)
    */
  def loadEigenVectors(path: String) : Int2ObjectArrayMap[DoubleArrayList] = {
    // Each row of path is an eigenvector.
    val eigenVectors = new Int2ObjectArrayMap[DoubleArrayList]

    val fileStream = Source.fromInputStream(
      new GZIPInputStream(
        new FileInputStream(
          new File(path)
        )
      )
    )

    logger.info("Loading eigenvectors from %s...".format(path))
    for ((line, index) <- fileStream.getLines().zipWithIndex) {
      val values = line.split("\t").map(_.toDouble)
      eigenVectors.put(index, new DoubleArrayList(values))
    }

    eigenVectors
  }

  /**
    * @return Vector of all elements of eigenVectors which correspond to wikiID.
    *         In other words: we build a vector where the i-th element is the
    *         wikiID-th value of the i-th eigenvector.
    */
  def eigenEmbeddingVector(wikiID: Int, embeddingSize: Int = 0) : DoubleArrayList = {
    val wikiIndex = WikiBVGraph.getNodeID(wikiID)
    val eigenEmbedding = new DoubleArrayList()

    val eigenSize = if (embeddingSize <= 0 || embeddingSize > eigenVectors.size())
                      eigenVectors.size()
                    else embeddingSize

    for(eigenIndex <- 0 until eigenSize) {
      val eigenValue = eigenVectors.get(0).getDouble(wikiIndex)
      eigenEmbedding.add(eigenValue)
    }

    eigenEmbedding
  }
}
