package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.latent.lda

import java.io.{File, FileInputStream}
import java.util.zip.GZIPInputStream

import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.{Embeddings, WikiEmbeddings}
import org.slf4j.LoggerFactory

import scala.io.Source


class LDA(topics: Int) extends Embeddings {
  protected override def logger = LoggerFactory.getLogger(getClass)


  protected override def loadEmbeddings() = loadEmbeddings(getTopicsPath)


  protected def getTopicsPath = Config.getString("wikipedia.latent.lda.topics") +
                                                     "%d/topical_documents.gz".format(topics)


  protected def loadEmbeddings(path: String): WikiEmbeddings = {
    val lda = new LDAEmbeddings()

    val reader = Source.fromInputStream(
      new GZIPInputStream(
        new FileInputStream(
          new File(path)
        )
      )
    )

    logger.info("Loading LDA embedding from %s...".format(path))
    for (line <- reader.getLines()) {
      val row = line.split("\t")
      val embedding = Array.fill[Float](100)(0f)

      row.slice(1, row.size).foreach {
        case indexValue =>

          val index = indexValue.split(":")(0).toInt
          val value = indexValue.split(":")(1).toFloat
          embedding(index) = value
      }

      val wikiID = Integer.parseInt(row(0))
      lda.putRow(wikiID, embedding)

    }
    logger.info("LDA embedding loaded!")

    lda
  }

}