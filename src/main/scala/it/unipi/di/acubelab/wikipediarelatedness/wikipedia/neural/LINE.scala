package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.neural

import java.nio.file.Paths
import java.util.zip.GZIPInputStream
import java.io.{File, FileInputStream}

import it.unimi.dsi.fastutil.floats.FloatArrayList
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unipi.di.acubelab.wikipediarelatedness.utils.{OldConfiguration, Similarity}
import org.slf4j.LoggerFactory

import scala.io.Source

class LINE(val size: Int, val order: Int, val negative: Int) {
  val logger = LoggerFactory.getLogger(classOf[LINE])

  val linePath = getLINEPath()
  val embeddings = loadLINEEmbeddings(linePath)


  def loadLINEEmbeddings(path: String) : Int2ObjectOpenHashMap[FloatArrayList] = {
    val reader = Source.fromInputStream(
      new GZIPInputStream(
        new FileInputStream(
          new File(path)
        )
      )
    )

    logger.info("Loading LINE embeddings with size: %d, order: %d and negative: %d..."
      .format(size, order, negative))

    val lineEmbeddings = new Int2ObjectOpenHashMap[FloatArrayList]()
    for (line <- reader.getLines().drop(1)) {
      val values = line.split(" ")

      val wikiID = values(0).toInt
      val emebedding = values.slice(1, values.length).map(_.toFloat)

      lineEmbeddings.put(wikiID, new FloatArrayList(emebedding))
    }

    lineEmbeddings
  }


  def getLINEPath() = {
    val directory =  OldConfiguration.wikipedia("line")
    val filename = "line_size%d_order%d_negative%d.gz".format(size, order, negative)

    Paths.get(directory, filename).toString
  }
}