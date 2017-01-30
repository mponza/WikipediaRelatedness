package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.latent

import java.io.{File, FileInputStream}
import java.util.zip.GZIPInputStream

import it.unimi.dsi.fastutil.floats.FloatArrayList
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.Embeddings
import org.slf4j.LoggerFactory

import scala.io.Source


class LDA extends Embeddings {
  protected override def logger = LoggerFactory.getLogger(getClass)


  override def loadEmbeddings(): Int2ObjectOpenHashMap[Seq[Tuple2[Int, Float]]] = {
    val path = Config.getString("wikipedia.latent.lda")

    // {wikiID -> [(index, value)}
    val lda = new Int2ObjectOpenHashMap[Seq[Tuple2[Int, Float]]](3219938)

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
      val embedding = Array.fill[Tuple2[Int, Float]](100)((0, 0f))

      row.slice(1, row.size).foreach {
        case indexValue =>

          val index = indexValue.split(":")(0).toInt
          val value = indexValue.split(":")(1).toFloat
          embedding(index) = (index, value)
      }

      // Update hash table with loaded embedding of wikiID
      val wikiID = Integer.parseInt(row(0))
      lda.putIfAbsent(wikiID, embedding)
    }
    logger.info("LDA embedding loaded!")

    lda
  }

}
