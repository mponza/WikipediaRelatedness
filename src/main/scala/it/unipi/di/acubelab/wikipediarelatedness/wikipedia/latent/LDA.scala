package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.latent

import java.io.{File, FileInputStream}
import java.util.zip.GZIPInputStream

import it.unimi.dsi.fastutil.floats.FloatArrayList
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.slf4j.LoggerFactory

import scala.io.Source

class LDA(path : String = OldConfiguration.lda("wiki_lda"), embeddingSize: Int = 100) {
  val logger = LoggerFactory.getLogger(classOf[LDA])
  lazy val embeddings = loadWikiLDAEmbedding(path, embeddingSize)

  def loadWikiLDAEmbedding(path: String, embeddingSize: Int): Int2ObjectOpenHashMap[FloatArrayList] = {
    // {wikiID -> [(index, value)}
    val lda = new Int2ObjectOpenHashMap[FloatArrayList]

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

      // Reads embedding of row
      val embedding = Array.fill[Float](embeddingSize)(0f)

      row.slice(1, row.size).foreach {
        case indexValue =>

          val index = indexValue.split(":")(0).toInt
          val value = indexValue.split(":")(1).toFloat
          embedding(index) = value
      }

      // Update hash table with loaded embedding of wikiID
      val wikiID = Integer.parseInt(row(0))
      lda.putIfAbsent(wikiID,  new FloatArrayList(embedding))
    }

    lda
  }

}
