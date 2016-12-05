package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.embeddings

import java.io.File
import java.util
import java.util.concurrent.TimeUnit

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.fastutil.objects.{Object2ObjectOpenHashMap, ObjectArrayList}
import it.unimi.dsi.logging.ProgressLogger
import it.unipi.di.acubelab.wat.dataset.embeddings.EmbeddingsDataset
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

/**
  * Pre-process wikiID present in wikiRelTasks by saving the most top-k similar entities
  * (k = 10000) into path, parameter specified by process method.
  *
  * @param wikiRelTasks
  */
class ProcessTopKEmbeddings(wikiRelTasks: List[WikiRelateTask]) {
  val logger = LoggerFactory.getLogger(classOf[ProcessTopKEmbeddings])

  // Single word entities.
  val wordEntities = (wikiRelTasks.map(task => "ent_%d".format(task.src.wikiID)) ++
                      wikiRelTasks.map(task => "ent_%d".format(task.dst.wikiID)) ).distinct

  // Word entity pairs.
  val wordEntityPairs = wikiRelTasks.map {
    case task => Tuple2("ent_%d".format(task.src.wikiID), "ent_%d".format(task.dst.wikiID))
  }.distinct


  /**
    * Main method which generates entity and entityPairs embedding and context emebddings, respectively.
    * @param dirPath
    * @param modelName
    */
  def generateTopK(dirPath: String, modelName: String) = {
    val topKEmbs = new TopKEmbeddings(modelName)

    logger.info("TopK Cache Embedding Generation...")

    generateEntity2Entities(dirPath, topKEmbs)
    generateEntityPairs2Entities(dirPath, topKEmbs)

    logger.info("TopK Cache Embedding Generation ended.")
  }


  //
  // Entity Pairs 2 Weighted Entities
  //

  /**
    * Generates srcEntity => [(dstEntity, weight)] of the context vector (average embedding of two entities).
    *
    * @param dirPath
    * @param topKEmbs
    */
  protected def generateEntityPairs2Entities(dirPath: String, topKEmbs: TopKEmbeddings) = {
    val path = new File(dirPath, "pairs2ents.bin").getAbsolutePath
    val entityPairs2entities = new Object2ObjectOpenHashMap[Tuple2[Int, Int], List[Tuple2[Int, Float]]]()

    val pl = new ProgressLogger(logger, 1, TimeUnit.MINUTES)
    pl.start("TopKSimilar Embedding Pairs of Entitites Processing...")

    wordEntityPairs.foreach {
      case pair: Tuple2[String, String] =>
        val srcWikiID = pair._1.substring(4, pair._1.length).toInt
        val dstWikiID = pair._2.substring(4, pair._2.length).toInt


        val contextEntities = topKEmbs.getTopK(srcWikiID, dstWikiID)

        entityPairs2entities.put(Tuple2(srcWikiID, dstWikiID), contextEntities)
        entityPairs2entities.put(Tuple2(dstWikiID, srcWikiID), contextEntities)

        pl.update()
    }

    pl.done()

    logger.info("EntityPairs2Entities ended.")

    logger.info("Serializing into file %s...".format(path))
    new File(path).getParentFile.mkdirs
    BinIO.storeObject(entityPairs2entities, path)
    logger.info("TopKSimilar Processing end.")
  }


  //
  // Entity 2 Weighted Entities
  //

  /**
    * Generates cache for srcEntity => [(dstEntity, weight)]
    *
    * @param dirPath
    * @param topKembs
    */
  protected def generateEntity2Entities(dirPath: String, topKembs: TopKEmbeddings) = {
    val path = new File(dirPath, "ent2ents.bin").getAbsolutePath
    val entity2entities = new Int2ObjectOpenHashMap[List[Tuple2[Int, Float]]]()

    val pl = new ProgressLogger(logger, 1, TimeUnit.MINUTES)
    pl.start("TopKSimilar Embedding Entitites Processing...")

    var minEnts = Integer.MAX_VALUE
    wordEntities.foreach {
      case wordEntity =>

        val wikiID = wordEntity.substring(4, wordEntity.length).toInt

        val entities = topKembs.getTopK(wikiID)

        minEnts = Math.min(entities.size, minEnts)

        entity2entities.put(wikiID, entities)

        pl.update()
    }
    logger.info("Minimum found: %d".format(minEnts))
    pl.done()

    logger.info("Serializing into file %s...".format(path))
    new File(path).getParentFile.mkdirs
    BinIO.storeObject(entity2entities, path)
    logger.info("TopKSimilar Processing end.")
  }


  /**
    * Returns WikiIDs of entities most smilar to wordEntity. Default threshold is 10000.
    *
    * @param wordEntity
    * @param embeddings
    * @return
    */
  protected def getTopKEntities(wordEntity: String, embeddings: EmbeddingsDataset) = {
    val it = embeddings.topKSimilarFromWord(wordEntity).iterator()
    wordIterator2entities(it)
  }


  /**
    * Returns list of WikiIDs from an iterator of words.
    * @param it
    */
  protected def wordIterator2entities(it: util.Iterator[String]) = {
    val topKEntities = ListBuffer.empty[Int]

    while (it.hasNext) {
      val s = it.next()
      try {
        if (s.startsWith("ent_")) topKEntities += s.substring(4, s.length).toInt
      } catch {
        case e: Exception => logger.info("Error with %s: %s".format(s.toString, e.toString))
      }
    }

    topKEntities.toList
  }


  /**
    * Weights topKentities by cosine similarity with entityWikiID.
    *
    * @param entityWikiID
    * @param topKentities
    * @param embeddings
    * @return
    */
  protected def weightEntities(entityWikiID: Int, topKentities: List[Int], embeddings: EmbeddingsDataset) = {
    topKentities.map {
      case wikiID: Int =>
        val weight = embeddings.similarity("ent_%d".format(entityWikiID), "ent_%d".format(wikiID))
        Tuple2(entityWikiID, weight)
    }
  }

}
