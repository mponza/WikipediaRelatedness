package it.unipi.di.acubelab.graphrel.dataset.wikisim

import java.io.{BufferedWriter, File, FileOutputStream, OutputStreamWriter}

import com.github.tototoshi.csv.CSVWriter
import it.unipi.di.acubelab.graphrel.dataset.{WikiEntity, WikiRelTask}
import it.unipi.di.acubelab.graphrel.utils.{Configuration, WAT}
import it.unipi.di.acubelab.graphrel.wikipedia.processing.webgraph.WikiBVGraph
import org.slf4j.LoggerFactory

class WikiSimProcessing(wikiSim: WikiSimDataset) {
  val logger = LoggerFactory.getLogger(classOf[WikiSimProcessing])
  logger.info("Loading WikiBVGraph...")

  def process() : Unit = {
    logger.info("Processing Dataset...")

    val normWikiSimPairs = normalize(wikiSim.wikiSimPairs)
    val redirWikiSimPairs = redirect(normWikiSimPairs)
    val filterWikiSimPairs = wikiFilter(redirWikiSimPairs)
    checkDuplicated(filterWikiSimPairs)

    store(filterWikiSimPairs, Configuration.dataset("procWikiSim"))

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
        val normalizedRel = (wikiRelTask.rel) / 10.0

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
        val srcRedWikiEntity = new WikiEntity(srcWikiID, srcWikiTitle)

        val (dstWikiTitle, dstWikiID) = WAT.redirect(wikiRelTask.dst.wikiTitle)
        val dstRedWikiEntity = new WikiEntity(dstWikiID, dstWikiTitle)

        new WikiRelTask(srcRedWikiEntity, wikiRelTask.srcWord,
                        dstRedWikiEntity, wikiRelTask.dstWord,
                        wikiRelTask.rel)
    }
  }

  /**
    * Removes pages which are not "real" Wikipedia Pages (e.g. disambiguation pages).
    * @param wikiRelTasks
    * @return
    */
  def wikiFilter(wikiRelTasks: List[WikiRelTask]) : List[WikiRelTask] = {
    logger.info("Filtering...")

    val realWikiPairs = wikiRelTasks.filter {
      case wikiRelTask: WikiRelTask =>
        val keep = WikiBVGraph.contains(wikiRelTask.src.wikiID) && WikiBVGraph.contains(wikiRelTask.dst.wikiID)
        if (!keep) {
          logger.warn("The following tuple: %s has been removed (Not present in the Wikipedia Graph)."
            .format(wikiRelTask))
        }
        keep
    }

    realWikiPairs
  }

  def checkDuplicated(wikiRelTasks: List[WikiRelTask]) = {
    val groupedPairs = wikiRelTasks.groupBy(wikiRelTask => "%s,%s"
      .format(wikiRelTask.src.wikiTitle, wikiRelTask.dst.wikiTitle))

    groupedPairs.foreach{
      case (strPair, pairTasks) =>
      if (pairTasks.length > 1) logger.warn("Duplicated pair %s".format(strPair))
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
