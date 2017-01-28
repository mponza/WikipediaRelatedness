package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.latent

import java.io.{File, FileInputStream}
import java.util.zip.GZIPInputStream

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.Embeddings
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory

import scala.io.Source

class SVD extends Embeddings {
  protected val logger = LoggerFactory.getLogger(classOf[SVD])
  protected val wikiGraph = WikiBVGraphFactory.makeWikiBVGraph("out")


  protected def loadEmbeddings() = loadEmbeddings(Config.getString("wikipedia.latent.svd.right"))


  /**
    * Loads eigenvectors from path where each row is an eigenvector.
    *
    * @return [i-th_eigenVector: eigenVector] (namely 0-th is the highest, 1-th the second highest, ...)
    */
  protected def loadEmbeddings(path: String) : Int2ObjectOpenHashMap[Seq[Tuple2[Int, Float]]] = {
    // Each row is an eigenvector.
    val svd = new Int2ObjectOpenHashMap[Seq[Tuple2[Int, Float]]]

    val reader = Source.fromInputStream(
      new GZIPInputStream(
        new FileInputStream(
          new File(path)
        )
      )
    )

    logger.info("Loading eigenvectors from %s...".format(path))
    for ((row, index) <- reader.getLines().zipWithIndex) {

      val embedding = Array.fill[Tuple2[Int, Float]](100)((0, 0f))
      row.split(" ").map(_.toFloat).zipWithIndex.foreach {
        case (value, i) =>
          embedding(i) = (i, value)
      }

      svd.putIfAbsent(index, embedding)
    }
    logger.info("Eigenvectors loaded!")

    svd
  }


  /**
    * Maps wikiID to the corresponding latent space.
    *
    * @return Vector made by all wikiID-th compontents of the first embeddingSize eigenvectors.
    */
  override def apply(wikiID: Int) : Seq[(Int, Float)] = super.apply(wikiGraph.getNodeID(wikiID))
}
