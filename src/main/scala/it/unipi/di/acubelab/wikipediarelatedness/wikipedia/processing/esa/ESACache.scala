package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa

import java.io.File
import java.nio.file.Paths

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.io.BinIO
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import org.slf4j.LoggerFactory


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
    val bodies = wikiIDs.par.map(wikiID => ESA.lucene.wikipediaBody(wikiID))
    logger.info("Retrieving concepts...")
    val concepts = bodies.par.map(body => ESA.wikipediaConcepts(body))

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
    * @return List of [wikiID, weight] of size threshold.
    */
  def get(wikiID: Int, threshold: Int = size) : List[Tuple2[Int, Float]] = {
    if (!wikiID2Concepts.containsKey(wikiID)) return null
    wikiID2Concepts.get(wikiID).slice(0, threshold)
  }
}

