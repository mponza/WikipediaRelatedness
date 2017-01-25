package it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim

import java.io.File

import com.github.tototoshi.csv.CSVReader
import it.unipi.di.acubelab.wikipediarelatedness.dataset.{WikiEntity, WikiRelateDataset, WikiRelateTask}
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config

import scala.collection.mutable


class WikiSimDataset() extends WikiRelateDataset {

  override def loadDataset(): Seq[WikiRelateTask] = {
    val path = Config.getString("dataset.wikisim")
    logger.info("Loading WikiSimDataset from %s".format(path))

    val pairs = new mutable.MutableList[WikiRelateTask]
    val csvReader = CSVReader.open(new File(path))

    csvReader .foreach {
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
    pairs
  }

  override def toString() : String = "WikiSim"
}