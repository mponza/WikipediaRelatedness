package it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim

import it.unipi.di.acubelab.wikipediarelatedness.dataset.{WikiEntity, WikiRelateDataset, WikiRelateTask}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.service.WAT
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory


/*
  * Procedure used to clean the WikiSim dataset. One of the first/worst Scala code I have written.
  * To used it add srcWord a
  *
  * Copy-pasted from an old commit. Please, refactor me.
  *
  *
  * @param wikiSim
  */
object WikiSimCleaner{
  val logger = LoggerFactory.getLogger(getClass)
  logger.info("Loading WikiBVGraph...")


  def mainTest(args: Array[String]): Unit = {
    WikiSimCleaner.process(new WikiSimMilneWittenDataset)
  }


  def process(wikiSim: WikiSimMilneWittenDataset) : Unit = {
    logger.info("Processing Dataset...")
    val normWikiSimPairs = normalize(wikiSim.toList)
    val redirWikiSimPairs = redirect(normWikiSimPairs)
    val filterWikiSimPairs = wikiFilter(redirWikiSimPairs)

    checkDuplicated(filterWikiSimPairs)
    //store(filterWikiSimPairs, Config.getString("dataset.mw_wikisim"))

    logger.info("Dataset has been processed!")
  }


  /**
    *
    * @return List of normalized Wikipedia Similarity Pairs between the specified range.
    */
  def normalize(wikiSimPairs: List[WikiSimTask]) : List[WikiSimTask] = {
    logger.info("Normalizing...")

    wikiSimPairs.map {
      case wikiRelateTask: WikiSimTask =>
        val normalizedRelatedness = wikiRelateTask.humanRelatedness / 10.0f

        new WikiSimTask(wikiRelateTask.src, wikiRelateTask.dst,  normalizedRelatedness, wikiRelateTask.srcWord, wikiRelateTask.dstWord)
    }
  }


  /**
    * @param wikiSimPairs
    * @return wikiSimPairs re-mapped when they are Wikipedia redirects.
    */
  def redirect(wikiSimPairs: List[WikiSimTask]) : List[WikiSimTask]  = {
    logger.info("Redirecting...")

    wikiSimPairs.map {
      case wikiRelTask: WikiSimTask =>

        val (srcWikiTitle, srcWikiID) = WAT.redirect(wikiRelTask.src.wikiTitle)
        val srcRedWikiEntity = new WikiEntity(srcWikiID, srcWikiTitle)

        val (dstWikiTitle, dstWikiID) = WAT.redirect(wikiRelTask.dst.wikiTitle)
        val dstRedWikiEntity = new WikiEntity(dstWikiID, dstWikiTitle)

        new WikiSimTask(srcRedWikiEntity, dstRedWikiEntity, wikiRelTask.humanRelatedness, wikiRelTask.srcWord, wikiRelTask.dstWord)
    }
  }


  /**
    * Removes pages which are not "real" Wikipedia Pages (e.g. disambiguation pages).
    *
    * @param wikiRelateTasks
    * @return
    */
  def wikiFilter(wikiRelateTasks: List[WikiSimTask]) : List[WikiSimTask] = {
    logger.info("Filtering...")

    val wikiGraph = WikiBVGraphFactory.make("out")

    var n = 0
    val realWikiPairs = wikiRelateTasks.filter {
      case wikiRelTask: WikiRelateTask =>

        val keep = wikiGraph.contains(wikiRelTask.src.wikiID) && wikiGraph.contains(wikiRelTask.dst.wikiID)
        if (!keep) {
          logger.warn("The following tuple: %s has been removed (Not present in the Wikipedia Graph, probably Disambiguation page)."
            .format(wikiRelTask))

          n += 1
        }

        keep
    }

    logger.warn("Removed %d disambiguation pages".format(n))
    realWikiPairs
  }


  def checkDuplicated(wikiRelateTasks: List[WikiSimTask]) = {
    val groupedPairs = wikiRelateTasks.groupBy(wikiRelTask => "%s,%s"
      .format(wikiRelTask.src.wikiTitle, wikiRelTask.dst.wikiTitle))

    groupedPairs.foreach{
      case (strPair, pairTasks) =>

        if (pairTasks.length > 1) logger.warn("Duplicated pair %s".format(strPair))
    }
  }


  def toCSVString(wikiRelateTask: WikiSimTask) : Seq[Any] = {
    Seq(wikiRelateTask.srcWord, wikiRelateTask.src.wikiID, wikiRelateTask.src.wikiTitle,
      wikiRelateTask.dstWord, wikiRelateTask.dst.wikiID, wikiRelateTask.dst.wikiTitle,
      wikiRelateTask.humanRelatedness)

  }


  /*
  def store(wikiSimPairs: List[WikiSimTask], path: String) : Unit = {
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
  */


}