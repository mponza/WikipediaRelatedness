package it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim

import java.io.File

import com.github.tototoshi.csv.CSVReader
import it.unipi.di.acubelab.wikipediarelatedness.dataset.{WikiEntity, WikiRelateTask}
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import org.slf4j.LoggerFactory

import scala.collection.mutable


class WikiSimMilneWittenDataset extends Traversable[WikiSimTask]  {

  protected val logger = LoggerFactory.getLogger(getClass)

  protected val wikiPairs = loadDataset

  def loadDataset(): Seq[WikiSimTask] = {
    val path = Config.getString("dataset.wikisim.milnewitten")

    logger.info("Loading WikiSimDataset from %s".format(path))

    val pairs = new mutable.MutableList[WikiSimTask]
    val csvReader = CSVReader.open(new File(path))

    csvReader.foreach {
      fields =>

        val srcWord = fields(0).toString
        if (srcWord != "") {

          val src = new WikiEntity(fields(1).toInt, fields(2).replaceAll(" ", "_"))

          val dstWord = fields(3)
          val dst = new WikiEntity(fields(4).toInt, fields(5).replaceAll(" ", "_"))

          val humanRelatedness = fields(6).toFloat

          pairs += new WikiSimTask(src, dst, humanRelatedness, fields(0), fields(3))

        }
    }
    csvReader.close()

    logger.info("WikiSimDataset loaded!")
    logger.info("Size %d".format(pairs.size))

    pairs
  }


  def foreach[U](f: (WikiSimTask) => U) = wikiPairs.foreach(wikiRelTask => f(wikiRelTask))

}
