package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.embeddings

import java.io.File
import java.util.concurrent.TimeUnit

import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.fastutil.objects.{Object2ObjectOpenHashMap, ObjectArrayList}
import it.unimi.dsi.logging.ProgressLogger
import it.unipi.di.acubelab.wat.dataset.embeddings.EmbeddingsDataset
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import org.slf4j.LoggerFactory

/**
  * Pre-process wikiID present in wikiRelTasks by saving the most top-k similar entities
  * (k = 10000) into path, parameter specified by process method.
  * @param wikiRelTasks
  */
class ProcessTopKEmbeddings(wikiRelTasks: List[WikiRelateTask]) {
  val wordEntities = (wikiRelTasks.map(task => "ent_%d".format(task.src.wikiID)) ++
                      wikiRelTasks.map(task => "ent_%d".format(task.dst.wikiID)) ).distinct

  val logger = LoggerFactory.getLogger(classOf[ProcessTopKEmbeddings])


  def generate(path: String, embeddings: EmbeddingsDataset) = {
    // Entity to its most similar WORD entities
    val entity2entities = new Object2ObjectOpenHashMap[String, ObjectArrayList[String]]()

    val pl = new ProgressLogger(logger, 1, TimeUnit.MINUTES)
    pl.start("TopKSimilar Embedding Entitites Processing...")

    var minEnts = Integer.MAX_VALUE
    wordEntities.foreach {
      case wordEntity =>

        val entities = new ObjectArrayList[String](embeddings.topKSimilar(wordEntity))
        minEnts = Math.min(entities.size(), minEnts)
        entity2entities.put(wordEntity, entities)

        pl.update()
    }
    logger.info("Minimum found: %d".format(minEnts))
    pl.done()

    logger.info("Serializing into file %s...".format(path))
    new File(path).getParentFile.mkdirs
    BinIO.storeObject(entity2entities, path)

    logger.info("TopKSimilar Processing end.")
  }

}
