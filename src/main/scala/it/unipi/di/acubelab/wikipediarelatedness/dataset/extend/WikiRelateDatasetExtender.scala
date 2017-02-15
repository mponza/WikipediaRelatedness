package it.unipi.di.acubelab.wikipediarelatedness.dataset.extend

import java.io.{File, PrintWriter}
import java.util.Locale
import java.util.concurrent.TimeUnit

import it.unimi.dsi.logging.ProgressLogger
import it.unipi.di.acubelab.wikipediarelatedness.dataset.{DatasetFactory, WikiRelateDataset, WikiRelateTask}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.{WikiBVDistance, WikiBVPageRank}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory

/**
  * Class used to extend the dataset with more information.
  *
  */
object WikiRelateDatasetExtender {
  protected val logger = LoggerFactory.getLogger(getClass)

  protected val distance = new WikiBVDistance()
  protected val pageRank = new WikiBVPageRank()


  def main(args: Array[String]): Unit = {
    logger.info("Extending WikiSim dataset...")
    val exwikisim = extend(DatasetFactory.make("wikisim"))
    save(exwikisim, "/tmp/wikisim.csv")

    logger.info("Extending WiRe dataset...")
    val exwire = extend(DatasetFactory.make("wire"))
    save(exwire, "/tmp/wire.csv")
  }


  /**
    * Extends dataset with more information.
    *
    * @param dataset
    */
  protected def extend(dataset: WikiRelateDataset) : Seq[ExWikiRelateTask] = {
    val pl = new ProgressLogger(logger, 1, TimeUnit.MINUTES)
    pl.start("Starting extending computation...")

    val exdataset = dataset.slice(0, 2).map {
      case task =>
        val extask = extend(task)
        pl.update()

        extask
    }
    pl.done()

    exdataset.toSeq
  }


  /**
    * Generate ExtendedWikiRelateTask with extended information.
    * Modify this and ExWikiRelateTask to add more information.
    *
    * @param task
    */
  protected def extend(task: WikiRelateTask) = {
    val extask = new ExWikiRelateTask(task)

    extask.srcInDegree = WikiBVGraphFactory.make("in").outdegree(task.src.wikiID)
    extask.srcOutDegree = WikiBVGraphFactory.make("out").outdegree(task.src.wikiID)

    extask.dstInDegree = WikiBVGraphFactory.make("in").outdegree(task.dst.wikiID)
    extask.dstOutDegree = WikiBVGraphFactory.make("out").outdegree(task.dst.wikiID)

    extask.distance = distance.getDistance(task.src.wikiID, task.dst.wikiID)

    extask.srcPageRank = pageRank.getPositionPageRank(task.src.wikiID).toFloat
    extask.dstPageRank = pageRank.getPositionPageRank(task.dst.wikiID).toFloat

    extask
  }


  protected def save(extasks: Seq[ExWikiRelateTask], outfile: String) = {
    logger.info("Saving file into %s...".format(outfile))

    val f = new PrintWriter(outfile)

    f.write(extasks.head.header() + "\n")
    extasks.foreach {
      case extask =>
        f.write(extask.toString + "\n")
    }

    f.close()
  }

}
