package it.unipi.di.acubelab.wikipediarelatedness.dataset.wire

import java.io.File

import com.github.tototoshi.csv.CSVReader
import it.unipi.di.acubelab.wikipediarelatedness.dataset.{WikiEntity, WikiRelateTask}
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer


class WiReDataset(path: String) extends Traversable[WikiRelateTask] {
  val logger = LoggerFactory.getLogger(classOf[WiReDataset])
  val nytPairs = loadNYTPairs(path)

  def loadNYTPairs(path: String) : List[WikiRelateTask]= {
    logger.info("Loading WiRe Dataset... %s".format(path))

    val pairs = ListBuffer.empty[WikiRelateTask]
    val csvReader = CSVReader.open(new File(path))

    csvReader.foreach {
      fields =>
        val src = new WikiEntity(fields(0).toInt, fields(1))

        val dstWord = fields(3)
        val dst = new WikiEntity(fields(2).toInt, fields(3))

        val humanRelatedness = fields(4).toFloat

        pairs += new WikiRelateTask(src, dst, humanRelatedness)
    }

    csvReader.close()

    logger.info("WiRe loaded!")
    pairs.toList
  }

  def foreach[U](f: (WikiRelateTask) => U) {
    nytPairs.foreach(nytTask => f(nytTask))
  }

  override def toString() : String = "WiRe"
}