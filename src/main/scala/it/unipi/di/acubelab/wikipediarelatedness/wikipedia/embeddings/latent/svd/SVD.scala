package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.latent.svd

import java.io.{File, FileInputStream}
import java.util.zip.GZIPInputStream

import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.{Embeddings, WikiEmbeddings}
import org.slf4j.LoggerFactory

import scala.io.Source

class SVD extends Embeddings {
  protected override def logger = LoggerFactory.getLogger(getClass)


  protected override def loadEmbeddings() = loadEmbeddings(Config.getString("wikipedia.latent.svd.right"))


  /**
    * Loads eigenvectors from path where each row is an eigenvector.
    *
    * @return [i-th_eigenVector: eigenVector] (namely 0-th is the highest, 1-th the second highest, ...)
    */
  protected def loadEmbeddings(path: String) : WikiEmbeddings = {
    val svd = new SVDEmbeddings

    val reader = Source.fromInputStream(
      new GZIPInputStream(
        new FileInputStream(
          new File(path)
        )
      )
    )


    logger.info("Loading eigenvectors from %s...".format(path))
    for ((row, index) <- reader.getLines().zipWithIndex) {

      //val embedding = new FloatArrayList(200)

      val embedding = Array.ofDim[Float](200)

      row.split("\t").map(_.toFloat).zipWithIndex.foreach {
        case (value, i) =>
          embedding(i) = value
      }

      svd.putRow(index, embedding)

    }
    logger.info("Eigenvectors loaded!")

    svd
  }

}
