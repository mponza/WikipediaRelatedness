package it.unipi.di.acubelab.wikipediarelatedness.dataset.nyt

import java.io.File

import com.github.tototoshi.csv.CSVReader
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

/**
  * Warning: Alpha data. Use it only for statistics.
  * @param path
  */
class NYTDataset(path: String) extends Traversable[NYTTask] {
  val logger = LoggerFactory.getLogger(classOf[NYTDataset])
  val nytPairs = loadNYTPairs(path)


  def loadNYTPairs(path: String) : List[NYTTask] = {
    logger.info("Loading NYT dataset %s...".format(path))

    val pairs = ListBuffer.empty[NYTTask]
    val csvReader = CSVReader.open(new File(path))

    csvReader.foreach {
      fields =>
        pairs += NYTTask.csv2NYTTask(fields)
    }
    csvReader.close()

    logger.info("NYTDataset loaded!")

    pairs.toList
  }


  def foreach[U](f: (NYTTask) => U) {
    nytPairs.foreach(nytTask => f(nytTask))
  }


  override def toString() : String = "NYT"
}