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


class WikiRelTask(_src: WikiEntity, _dst: WikiEntity, _rel: Double) {
  val src = _src
  val dst = _dst
  val rel = _rel  // Human Relatedness

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
  val rawWikiSimPairs = loadWikiSimPairs(url)

  val wikiSimPairs = normalizedWikiSimPairs((0, 10))

  /**
    *
    * @param url
    * @return List of WikiSimPair with the corresponding human relatedness.
    */
  def loadWikiSimPairs(url: URL) : List[WikiRelTask]= {
    logger.info("Loading WikiSimDataset...")

    val pairs = new mutable.MutableList[WikiRelTask]
    val csvReader = CSVReader.open(new File(url.getPath))

    csvReader.foreach {
      fields =>
        val src = new WikiEntity(fields(1).toInt, fields(2).replaceAll(" ", "_"))
        val dst = new WikiEntity(fields(4).toInt, fields(5).replaceAll(" ", "_"))

        val rel = fields(6).toDouble

        pairs += new WikiRelTask(src, dst, rel)
    }

    csvReader.close()
    pairs.toList
  }

  /**
    *
    * @param range
    * @return List of normalized Wikipedia Similarity Pairs between the specified range.
    */
  def normalizedWikiSimPairs(range: (Float, Float) ) : List[WikiRelTask] = {
    rawWikiSimPairs.map {
      wikiRelTask =>
        val normalizedRel = (wikiRelTask.rel - range._1) / (range._2 - range._1)
        new WikiRelTask(wikiRelTask.src, wikiRelTask.dst, normalizedRel)
    }
  }

  def foreach[U](f: (WikiRelTask) => U) {
    wikiSimPairs.foreach(wikiRelTask => f(wikiRelTask))
  }

  override def toString() : String = "WikiSim412"
}
