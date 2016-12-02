package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa

import java.io.File
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.logging.ProgressLogger
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer


/**
  * Cache between a wikiID to its vector of concepts.
  * @param dirPath
  * @param size
  */
class ESACache(val dirPath: String = Configuration.wikipedia("esaCache"), val size: Int = 10000) {
  val logger = LoggerFactory.getLogger(classOf[ESACache])
  protected lazy val wikiID2Concepts = loadCache()


  /***
    * Method used to generate the cache from a dataset.
    *
    * @param tasks
    */
  def generateCache(tasks: List[WikiRelateTask]) = {
    val wikiIDs = tasks.foldLeft(List.empty[Int])((IDs, task) => IDs ++ List(task.src.wikiID, task.dst.wikiID)).distinct

    logger.info("Retrieving bodies...")
    val bodies = wikiIDs.map(wikiID => ESA.lucene.wikipediaBody(wikiID))

    val pl = new ProgressLogger(logger, 1, TimeUnit.MINUTES)
    pl.start("Retrieving concepts...")

    val concepts = ListBuffer.empty[List[Tuple2[Int, Float]]]
    bodies.foreach {
      case body =>
        concepts += ESA.wikipediaConcepts(body, size)
        pl.update()
    }

    pl.done()

    logger.info("Building wikiID concepts mapping...")
    val wikiID2Concepts = new Int2ObjectOpenHashMap[List[Tuple2[Int, Float]]]()
    wikiIDs.zipWithIndex.foreach {
      case(wikiID: Int, index: Int) =>

        val wikiConcepts = concepts(index)
        wikiID2Concepts.put(wikiID, wikiConcepts)
    }

    logger.info("Serializing wikiIDs-concepts mapping...")
    new File(dirPath).mkdirs
    BinIO.storeObject(wikiID2Concepts, getPath())
  }


  protected def getPath() = Paths.get(dirPath, "cache_%d.bin".format(size)).toString


  protected def loadCache() = {
    logger.info("Loading ESA cache %s...".format(getPath()))
    try {
      BinIO.loadObject(getPath()).asInstanceOf[Int2ObjectOpenHashMap[List[Tuple2[Int, Float]]]]
    } catch {
      case e: Exception =>
        logger.warn("Cache not found, no cache will be used.")
        new Int2ObjectOpenHashMap[List[Tuple2[Int, Float]]]()  // empty cache
    }
  }


  /***
    *
    * @param wikiID
    * @param threshold
    * @return List of [wikiID, weight] of size threshold, sorted by threshold.
    */
  def get(wikiID: Int, threshold: Int = size) : List[Tuple2[Int, Float]] = {
    if (!wikiID2Concepts.containsKey(wikiID)) return null
    wikiID2Concepts.get(wikiID).slice(0, threshold)
  }
}

