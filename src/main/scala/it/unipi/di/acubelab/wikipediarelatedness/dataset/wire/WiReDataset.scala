package it.unipi.di.acubelab.wikipediarelatedness.dataset.wire

import java.io.File

import com.github.tototoshi.csv.CSVReader
import it.unipi.di.acubelab.wikipediarelatedness.dataset.{WikiRelateDataset, WikiEntity, WikiRelateTask}

import scala.collection.mutable.ListBuffer


/**
  * Trait of the WiRe Dataset.
  *
  */
trait WiReDataset extends WikiRelateDataset {

  /**
    * Path to salient-salient, non-salient-salient or non-salient-non-salient file.
    *
    * @return
    */
  protected def wirePath(): String

  override def loadDataset() : Seq[WikiRelateTask] = {
    val path = wirePath()
    logger.info("Loading WiRe Dataset... %s".format(path))

    val pairs = ListBuffer.empty[WikiRelateTask]
    val csvReader = CSVReader.open(new File(path))

    csvReader.toStream.drop(1).foreach {
      fields =>
        val src = new WikiEntity(fields(0).toInt, fields(1))

        val dstWord = fields(3)
        val dst = new WikiEntity(fields(6).toInt, fields(7))

        val humanRelatedness = fields(20).toFloat

        pairs += new WikiRelateTask(src, dst, humanRelatedness)
    }

    csvReader.close()

    logger.info("WiRe loaded!")
    pairs.toList
  }


  override def toString() : String = "WiRe"
}