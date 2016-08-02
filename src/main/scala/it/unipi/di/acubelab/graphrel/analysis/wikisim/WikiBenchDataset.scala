/*package it.unipi.di.acubelab.graphrel.analysis.wikisim

import java.io.File

import com.github.tototoshi.csv.CSVReader
import it.unipi.di.acubelab.graphrel.dataset.WikiEntity
import it.unipi.di.acubelab.graphrel.dataset.wikisim.WikiSimDataset
import org.slf4j.LoggerFactory

import scala.collection.mutable

class WikiBenchDataset(path: String) extends WikiSimDataset(path) {
    override val logger = LoggerFactory.getLogger(classOf[WikiBenchDataset])
    override val wikiSimPairs = loadWikiSimPairs(path)

    override def loadWikiSimPairs(path: String) : List[WikiBenchTask]= {
      logger.info("Loading WikiSimDataset...")

      val pairs = new mutable.MutableList[WikiBenchTask]
      val csvReader = CSVReader.open(new File(path))

      csvReader.foreach {
        fields =>
          val srcWord = fields(0).toString
          val src = new WikiEntity(fields(1).toInt, fields(2).replaceAll(" ", "_"))

          val dstWord = fields(3)
          val dst = new WikiEntity(fields(4).toInt, fields(5).replaceAll(" ", "_"))

          val rel = fields(6).toFloat
          val computeRel = fields(7).toFloat

          pairs += new WikiBenchTask(src, srcWord, dst, dstWord, rel, computeRel)
      }

      csvReader.close()

      logger.info("WikiBenchDataset loaded!")
      pairs.toList
    }


    def foreach[U](f: (WikiBenchTask) => U) {
      wikiSimPairs.foreach(wikiBenchTask => f(wikiBenchTask))
    }

    override def toString() : String = "WikiBench412"
}
*/