package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.latent

import java.io.{File, FileInputStream}
import java.util.zip.GZIPInputStream

import it.unimi.dsi.fastutil.floats.FloatArrayList
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.logging.ProgressLogger
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.{Embeddings, SeqFloatArrayList}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory

import scala.io.Source

class SVD extends Embeddings {
  protected override def logger = LoggerFactory.getLogger(getClass)
  protected val wikiGraph = WikiBVGraphFactory.makeWikiBVGraph("out")


  protected override def loadEmbeddings() = loadEmbeddings(Config.getString("wikipedia.latent.svd.right"))


  /**
    * Loads eigenvectors from path where each row is an eigenvector.
    *
    * @return [i-th_eigenVector: eigenVector] (namely 0-th is the highest, 1-th the second highest, ...)
    */
  protected def loadEmbeddings(path: String) : Int2ObjectOpenHashMap[Seq[Tuple2[Int, Float]]] = {
    // Each row is an eigenvector.

    val svd = new Int2ObjectOpenHashMap[Seq[Tuple2[Int, Float]]](4730474)

    val reader = Source.fromInputStream(
      new GZIPInputStream(
        new FileInputStream(
          new File(path)
        )
      )
    )

    logger.info("Loading eigenvectors from %s...".format(path))
    for ((row, index) <- reader.getLines().zipWithIndex) {

      val embedding = new FloatArrayList(200)
      row.split("\t").map(_.toFloat).zipWithIndex.foreach {
        case (value, i) =>
          embedding.add(i, value)
      }

      svd.put(index, new SeqFloatArrayList(embedding))
      println(index)

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
