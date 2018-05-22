package it.unipi.di.acubelab.wikipediarelatedness.evaluation.dataset

import java.io.File

import com.github.tototoshi.csv.CSVReader
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.task.{WikiEntity, WikiRelTask}
import org.slf4j.LoggerFactory

abstract class WikiRelCSVDataset extends WikiRelDataset {
  private val logger = LoggerFactory.getLogger(classOf[WikiRelCSVDataset])


  /**
    * Load CSV dataset whose header has format "srcWikiID,srcWikiTitle,dstWikiID,dstWikiTitle,relatedness".
    * @param filename
    * @return
    */
  def loadCSVDataset(filename: String) : Seq[WikiRelTask] = {

      val pairs = new scala.collection.mutable.MutableList[WikiRelTask]
      val csvReader = CSVReader.open(new File(filename))

      csvReader.toStream.drop(1).foreach {
        fields =>

          val src = new WikiEntity(fields(0).toInt, fields(1).replaceAll(" ", "_"))
          val dst = new WikiEntity(fields(2).toInt, fields(3).replaceAll(" ", "_"))
          val humanRelatedness = fields(4).toFloat

          pairs += new WikiRelTask(src, dst, humanRelatedness)

      }
      csvReader.close()

      pairs
    }
}
