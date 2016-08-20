package it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim

import java.io.File

import com.github.tototoshi.csv.CSVReader
import it.unipi.di.acubelab.wikipediarelatedness.dataset.{RelatednessDataset, WikiEntity, WikiRelTask}
import org.slf4j.LoggerFactory

import scala.collection.mutable


class WikiSimDataset(path: String) extends RelatednessDataset {
  val logger = LoggerFactory.getLogger(classOf[WikiSimDataset])

  val wikiSimPairs = loadWikiSimPairs(path)

  /**
    *
    * @return List of WikiSimPair with the corresponding human relatedness.
    */
  def loadWikiSimPairs(path: String) : List[WikiRelTask]= {
    logger.info("Loading WikiSimDataset... %s".format(path))

    val pairs = new mutable.MutableList[WikiRelTask]
    val csvReader = CSVReader.open(new File(path))

    csvReader.foreach {
      fields =>
        val srcWord = fields(0).toString
        val src = new WikiEntity(fields(1).toInt, fields(2).replaceAll(" ", "_"))

        val dstWord = fields(3)
        val dst = new WikiEntity(fields(4).toInt, fields(5).replaceAll(" ", "_"))

        val rel = fields(6).toDouble
        val computedRel = if (fields.length != 8) Double.NaN else fields(7).toDouble

        pairs += new WikiRelTask(src, srcWord, dst, dstWord, rel, computedRel)
    }

    csvReader.close()

    logger.info("WikiSimDataset loaded!")
    pairs.toList
  }


  def foreach[U](f: (WikiRelTask) => U) {
    wikiSimPairs.foreach(wikiRelTask => f(wikiRelTask))
  }

  override def toString() : String = "WikiSim412"
}
