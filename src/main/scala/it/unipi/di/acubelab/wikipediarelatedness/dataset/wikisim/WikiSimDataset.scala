package it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim

import java.io.File

import com.github.tototoshi.csv.CSVReader
import it.unipi.di.acubelab.wikipediarelatedness.dataset.{RelatednessDataset, WikiEntity, WikiRelTask}
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
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

        val humanClass = rel2Class(computedRel)

        pairs += new WikiRelTask(src, srcWord, dst, dstWord, rel, computedRel, humanClass)
    }

    csvReader.close()

    logger.info("WikiSimDataset loaded!")
    pairs.toList
  }

  def rel2Class(rel: Double) : Int = {
    val intervals = Configuration.intervals
    if (rel >= intervals("white")._1 && rel <= intervals("white")._2) {
      0
    } else if (rel > intervals("gray")._1 && rel < intervals("gray")._2) {
      2
    } else if (rel > intervals("black")._1 && rel < intervals("black")._2) {
      1
    } else {
      throw new IllegalArgumentException("Error while mapping relatedenss to class: %1.2f not in range.".format(rel))
    }
  }

  def foreach[U](f: (WikiRelTask) => U) {
    wikiSimPairs.foreach(wikiRelTask => f(wikiRelTask))
  }

  override def toString() : String = "WikiSim412"
}
