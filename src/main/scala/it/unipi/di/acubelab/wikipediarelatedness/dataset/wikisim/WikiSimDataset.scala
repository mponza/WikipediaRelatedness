package it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim

import java.io.File

import com.github.tototoshi.csv.CSVReader
import it.unipi.di.acubelab.wikipediarelatedness.dataset.{RelatednessDataset, WikiEntity, WikiRelateTask}
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import org.slf4j.LoggerFactory

import scala.collection.mutable


class WikiSimDataset(path: String = Configuration.dataset("procWikiSim")) extends RelatednessDataset {
  val logger = LoggerFactory.getLogger(classOf[WikiSimDataset])
  val wikiPairs = loadWikiPairs(path)

  /**
    *
    * @return List of WikiSimPair with the corresponding human relatedness.
    */
  def loadWikiPairs(path: String) : List[WikiRelateTask]= {
    logger.info("Loading WikiSimDataset... %s".format(path))

    val pairs = new mutable.MutableList[WikiRelateTask]
    val csvReader = CSVReader.open(new File(path))

    csvReader.foreach {
      fields =>
        val srcWord = fields(0).toString
        val src = new WikiEntity(fields(1).toInt, fields(2).replaceAll(" ", "_"))

        val dstWord = fields(3)
        val dst = new WikiEntity(fields(4).toInt, fields(5).replaceAll(" ", "_"))

        val humanRelatedness = fields(6).toFloat

        pairs += new WikiRelateTask(src, dst, humanRelatedness)
    }

    csvReader.close()

    logger.info("WikiSimDataset loaded!")
    pairs.toList
  }

  def foreach[U](f: (WikiRelateTask) => U) {
    wikiPairs.slice(0, 1).foreach(wikiRelTask => f(wikiRelTask))
  }

  override def toString() : String = "WikiSim412"
}