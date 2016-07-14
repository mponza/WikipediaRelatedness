package it.unipi.di.acubelab.graphrel.dataset

import java.io.File
import java.net.URL

import com.github.tototoshi.csv.CSVReader
import it.unipi.di.acubelab.graphrel.utils.Configuration
import org.slf4j.LoggerFactory

import scala.collection.mutable


class WikiEntity(_wikiID: Int, _wikiTitle: String) {
  val wikiID = _wikiID
  val wikiTitle = _wikiTitle
}


class WikiSimPair(_src: WikiEntity, _dst: WikiEntity, _rel: Double) {
  val src = _src
  val dst = _dst
  val rel = _rel

  def toList : List[Any] = {
    List(src.wikiID, dst.wikiID, src.wikiTitle, dst.wikiTitle, rel)
  }

  override def toString : String = {
    "%d,%d,%s,%s,%.2f".format(src.wikiID, dst.wikiID, src.wikiTitle, dst.wikiTitle, rel)
  }
}


class WikiSimDataset extends RelatednessDataset {
  val logger = LoggerFactory.getLogger(classOf[WikiSimDataset])

  val url = Configuration.dataset.wikiSim
  val wikiSimPairs = loadWikiSimPairs(url)

  /**
    *
    * @param url
    * @return List of WikiSimPair with the corresponding human relatedness.
    */
  def loadWikiSimPairs(url: URL) : List[WikiSimPair]= {
    logger.info("Loading WikiSimDataset...")

    val pairs = new mutable.MutableList[WikiSimPair]
    val csvReader = CSVReader.open(new File(url.getPath))

    csvReader.foreach {
      fields =>
        val src = new WikiEntity(fields(1).toInt, fields(2).replaceAll(" ", "_"))
        val dst = new WikiEntity(fields(4).toInt, fields(5).replaceAll(" ", "_"))

        val rel = fields(6).toDouble

        pairs += new WikiSimPair(src, dst, rel)
    }

    csvReader.close()
    pairs.toList
  }

  /**
    *
    * @param range
    * @return List of normalized Wikipedia Similarity Pairs between the specified range.
    */
  def normalizedWikiSimPairs(range: (Float, Float) = (0.0f, 1.0f)) : List[WikiSimPair] = {
    wikiSimPairs.map {
      wikiSimPair =>
        val normalizedRel = (wikiSimPair.rel - range._1) / (range._2 - range._1)
        new WikiSimPair(wikiSimPair.src, wikiSimPair.dst, normalizedRel)
    }
  }

  def foreach[U](f: (WikiSimPair) => U) {
    wikiSimPairs.foreach(wikiSimPair => f(wikiSimPair))
  }
}
