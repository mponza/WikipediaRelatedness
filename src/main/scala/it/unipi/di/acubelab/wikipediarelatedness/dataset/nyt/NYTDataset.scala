package it.unipi.di.acubelab.wikipediarelatedness.dataset.nyt

import java.io.File

import com.github.tototoshi.csv.CSVReader
import it.unipi.di.acubelab.wikipediarelatedness.dataset.{RelatednessDataset, WikiEntity, WikiRelateTask}
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

/**
  * Warning: Alpha data. Use it only for statistics.
  * @param path
  */
class NYTDataset(path: String) extends RelatednessDataset {
  val logger = LoggerFactory.getLogger(classOf[NYTDataset])
  val wikiPairs = loadNYTPairs(path)


  def loadNYTPairs(path: String) : List[WikiRelateTask] = {
    logger.info("Loading NYT dataset %s...".format(path))

    val pairs = ListBuffer.empty[WikiRelateTask]
    val csvReader = CSVReader.open(new File(path))

    csvReader.foreach {
      fields =>
        val srcEntity = new WikiEntity(fields(0).toInt, fields(1).toString)
        val dstEntity = new WikiEntity(fields(3).toInt, fields(4).toString)

        val wikiPair = new WikiRelateTask(srcEntity, dstEntity, -1)
        pairs += wikiPair
    }
    csvReader.close()

    logger.info("NYTDataset loaded!")

    pairs.toList
  }


  def foreach[U](f: (WikiRelateTask) => U) {
    wikiPairs.foreach(wikiRelTask => f(wikiRelTask))
  }


  override def toString() : String = "NYT"
}