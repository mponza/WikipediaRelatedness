package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.neural.line

import java.io.{File, FileInputStream}
import java.nio.file.Paths
import java.util.Locale
import java.util.zip.GZIPInputStream

import it.unimi.dsi.fastutil.floats.FloatArrayList
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import org.slf4j.LoggerFactory

import scala.io.Source

import  it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.Embeddings


/**
  * Embeddings generated with LINE.
  *
  * @param size
  * @param order
  * @param negative
  * @param sample
  * @param rho
  */
class LINE(val size: Int, val order: Int, val negative: Int, val sample: Int, val rho: Float) extends Embeddings {
  protected override def logger = LoggerFactory.getLogger(getClass)


  override def loadEmbeddings() = {
    val path = getLINEPath

    val reader = Source.fromInputStream(
      new GZIPInputStream(
        new FileInputStream(
          new File(path)
        )
      )
    )

    logger.info("Loading LINE embeddings with size: %d, order: %d, negative: %d, sample: %d and rho: %1.3f..."
      .format(size, order, negative, sample, rho))

    val lineEmbeddings = new Int2ObjectOpenHashMap[FloatArrayList]()
    for (line <- reader.getLines()) {
      val values = line.split(" ")

      val wikiID = values(0).toInt
      val emebedding = values.slice(1, values.length).map(_.toFloat)
      
      lineEmbeddings.put(wikiID, new FloatArrayList(emebedding))
    }


    new LINEEmbeddings(lineEmbeddings)
  }


  protected def getLINEPath = {
    val directory =  Config.getString("wikipedia.neural.line")
    val filename = "embeddings_size:%d,order:%d,neg:%d,sample:%d,rho:%1.3f.gz"
                    .formatLocal(Locale.US, size, order, negative, sample, rho)

    Paths.get(directory, filename).toString
  }
}