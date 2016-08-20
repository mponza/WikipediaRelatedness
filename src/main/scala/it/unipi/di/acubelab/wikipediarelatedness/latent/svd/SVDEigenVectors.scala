package it.unipi.di.acubelab.wikipediarelatedness.latent.svd

import java.io.{File, FileInputStream}
import java.util.zip.GZIPInputStream

import it.unimi.dsi.fastutil.doubles.DoubleArrayList
import it.unimi.dsi.fastutil.ints.{Int2ObjectArrayMap, IntArrayList}
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.WikiBVGraph

import scala.io.Source

class SVDEigenVectors(path : String = Configuration.eigen("left")) {
  val eigenVectors = loadEigenVectors(path)

  /**
    * Loads eigen vectors from path where each row is a eigenvector of ~4M of doubles.
    * @return {i-th_eigenVector: eigenVector} (0-th is the highest, 1-th the second highest, ...)
    */
  def loadEigenVectors(path: String) : Int2ObjectArrayMap[DoubleArrayList] = {
    // Each row of path is an eigen vector.
    val eigenVectors = new Int2ObjectArrayMap[DoubleArrayList]

    val fileStream = Source.fromInputStream(
      new GZIPInputStream(
        new FileInputStream(
          new File(path)
        )
      )
    )

    for ((line, index) <- fileStream.getLines().zipWithIndex) {
      val values = line.split("\t").map(_.toDouble)
      eigenVectors.put(index, new DoubleArrayList(values))
    }

    eigenVectors
  }

  def eigenEmbeddingVector(wikiID: Int) : DoubleArrayList = {

  }
}
