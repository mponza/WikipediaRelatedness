package it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim
/*
import java.io.File
import java.util.Locale

import com.github.tototoshi.csv.CSVWriter
import it.unipi.di.acubelab.wikipediarelatedness.dataset.{WikiEntity, WikiRelateDataset, WikiRelateTask}
import it.unipi.di.acubelab.wikipediarelatedness.utils.{Config, WAT}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory


/
  * Procedure used to clean the WikiSim dataset. One of the first/worst Scala code I have written.
  * To used it add srcWord a
  *
  * Copy-pasted from an old commit. Please, refactor me.
  *
  *
  * @param wikiSim

class WikiSimCleaner(wikiSim: WikiRelateDataset) {
  val logger = LoggerFactory.getLogger(getClass)
  logger.info("Loading WikiBVGraph...")


  def process() : Unit = {
    logger.info("Processing Dataset...")
    val normWikiSimPairs = normalize(wikiSim.toList)
    val redirWikiSimPairs = redirect(normWikiSimPairs)
    val filterWikiSimPairs = wikiFilter(redirWikiSimPairs)

    checkDuplicated(filterWikiSimPairs)
    store(filterWikiSimPairs, Config.getString("dataset.mw_wikisim"))

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
        val normalizedRelatedness = wikiRelateTask.humanRelatedness / 10.0f

        new WikiRelateTask(wikiRelateTask.src, wikiRelateTask.dst,  normalizedRelatedness, wikiRelateTask.srcWord, wikiRelateTask.dstWord)
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

        new WikiRelateTask(srcRedWikiEntity, dstRedWikiEntity, wikiRelTask.humanRelatedness, wikiRelTask.srcWord, wikiRelTask.dstWord)
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

    val wikiGraph = WikiBVGraphFactory.make("out")

    val realWikiPairs = wikiRelateTasks.filter {
      case wikiRelTask: WikiRelateTask =>

        val keep = wikiGraph.contains(wikiRelTask.src.wikiID) && wikiGraph.contains(wikiRelTask.dst.wikiID)
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
        println(toCSVString(wikiRelateTask))
        csvWriter.writeRow(toCSVString(wikiRelateTask))
    }

    csvWriter.close
  }

  def toCSVString(wikiRelateTask: WikiRelateTask) : Seq[Any] = {
    Seq(wikiRelateTask.srcWord, wikiRelateTask.src.wikiID, wikiRelateTask.src.wikiTitle,
      wikiRelateTask.dstWord, wikiRelateTask.dst.wikiID, wikiRelateTask.dst.wikiTitle,
      wikiRelateTask.humanRelatedness)

  }

}*/