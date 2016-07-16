package it.unipi.di.acubelab.graphrel.dataset.wikisim

import java.io.File

import com.github.tototoshi.csv.CSVWriter
import it.unipi.di.acubelab.graphrel.dataset.WikiRelTask
import it.unipi.di.acubelab.graphrel.utils.{Configuration, WAT}
import org.slf4j.LoggerFactory

class WikiSimProcessing(wikiSim: WikiSimDataset) {
  val logger = LoggerFactory.getLogger(classOf[WikiSimProcessing])

  def process() : Unit = {
    logger.info("Processing Dataset...")

    val normWikiSimPairs = normalize(wikiSim.wikiSimPairs)
    val redirWikiSimPairs = redirect(normWikiSimPairs)

    store(redirWikiSimPairs, Configuration.dataset("procWikiSim"))

    logger.info("Dataset has been processed!")
  }

  /**
    *
    * @return List of normalized Wikipedia Similarity Pairs between the specified range.
    */
  def normalize(wikiSimPairs: List[WikiRelTask]) : List[WikiRelTask] = {
    logger.info("Normalizing...")

    wikiSimPairs.map {
      case wikiRelTask: WikiRelTask =>
        val normalizedRel = (wikiRelTask.rel) / 10f

        new WikiRelTask(wikiRelTask.src, wikiRelTask.srcWord,
                        wikiRelTask.dst, wikiRelTask.dstWord,
                        normalizedRel)
    }
  }

  /**
    * @param wikiSimPairs
    * @return wikiSimPairs re-mapped when they are Wikipedia redirects.
    */
  def redirect(wikiSimPairs: List[WikiRelTask]) : List[WikiRelTask]  = {
    logger.info("Redirecting...")

    wikiSimPairs.map {
      case wikiRelTask: WikiRelTask =>
        val (srcWikiTitle, srcWikiID) = WAT.redirect(wikiRelTask.src.wikiTitle)
        val (dstWikiTitle, dstWikiID) = WAT.redirect(wikiRelTask.dst.wikiTitle)

        new WikiRelTask(wikiRelTask.src, wikiRelTask.srcWord,
                        wikiRelTask.dst, wikiRelTask.dstWord,
                        wikiRelTask.rel)
    }
  }

  def store(wikiSimPairs: List[WikiRelTask], path: String) : Unit = {
    logger.info("Writing...")

    new File(path).getParentFile.mkdirs
    val csvWriter = CSVWriter.open(path)

    wikiSimPairs.foreach {
      case wikiRelTask: WikiRelTask =>
        csvWriter.writeRow(wikiRelTask.toList)
    }

    csvWriter.close
  }

}
