package it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim

import java.io.{BufferedWriter, File, FileOutputStream, OutputStreamWriter}
import java.util.Locale

import com.github.tototoshi.csv.CSVWriter
import it.unipi.di.acubelab.wikipediarelatedness.dataset.{WikiEntity, WikiRelateTask}
import it.unipi.di.acubelab.wikipediarelatedness.utils.{Configuration, WAT}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.WikiBVGraph
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
  def normalize(wikiSimPairs: List[WikiRelateTask]) : List[WikiRelateTask] = {
    logger.info("Normalizing...")

    wikiSimPairs.map {
      case wikiRelateTask: WikiRelateTask =>
        val normalizedRelatedness = (wikiRelateTask.humanRelatedness) / 10.0

        new WikiRelateTask(wikiRelateTask.src, wikiRelateTask.dst,  normalizedRelatedness)
    }
  }

  /**
    * @param wikiSimPairs
    * @return wikiSimPairs re-mapped when they are Wikipedia redirects.
    */
  def redirect(wikiSimPairs: List[WikiRelateTask]) : List[WikiRelateTask]  = {
    logger.info("Redirecting...")

    wikiSimPairs.map {
      case wikiRelTask: WikiRelateTask =>
        val (srcWikiTitle, srcWikiID) = WAT.redirect(wikiRelTask.src.wikiTitle)
        val srcRedWikiEntity = new WikiEntity(srcWikiID, srcWikiTitle)

        val (dstWikiTitle, dstWikiID) = WAT.redirect(wikiRelTask.dst.wikiTitle)
        val dstRedWikiEntity = new WikiEntity(dstWikiID, dstWikiTitle)

        new WikiRelateTask(srcRedWikiEntity, dstRedWikiEntity, wikiRelTask.humanRelatedness)
    }
  }

  /**
    * Removes pages which are not "real" Wikipedia Pages (e.g. disambiguation pages).
    *
    * @param wikiRelateTasks
    * @return
    */
  def wikiFilter(wikiRelateTasks: List[WikiRelateTask]) : List[WikiRelateTask] = {
    logger.info("Filtering...")

    val realWikiPairs = wikiRelateTasks.filter {
      case wikiRelTask: WikiRelateTask =>
        val keep = WikiBVGraph.contains(wikiRelTask.src.wikiID) && WikiBVGraph.contains(wikiRelTask.dst.wikiID)
        if (!keep) {
          logger.warn("The following tuple: %s has been removed (Not present in the Wikipedia Graph)."
            .format(wikiRelTask))
        }
        keep
    }

    realWikiPairs
  }

  def checkDuplicated(wikiRelateTasks: List[WikiRelateTask]) = {
    val groupedPairs = wikiRelateTasks.groupBy(wikiRelTask => "%s,%s"
      .format(wikiRelTask.src.wikiTitle, wikiRelTask.dst.wikiTitle))

    groupedPairs.foreach{
      case (strPair, pairTasks) =>
      if (pairTasks.length > 1) logger.warn("Duplicated pair %s".format(strPair))
    }
  }

  def store(wikiSimPairs: List[WikiRelateTask], path: String) : Unit = {
    logger.info("Writing...")

    new File(path).getParentFile.mkdirs
    val csvWriter = CSVWriter.open(path)

    wikiSimPairs.foreach {
      case wikiRelateTask: WikiRelateTask =>
        csvWriter.writeRow(toCSVString(wikiRelateTask))
    }

    csvWriter.close
  }

  def toCSVString(wikiRelateTask: WikiRelateTask) : String = {
    "%s,,%s,,%1.f".formatLocal(Locale.US, wikiRelateTask.src, wikiRelateTask.dst, wikiRelateTask.humanRelatedness)
  }

}
