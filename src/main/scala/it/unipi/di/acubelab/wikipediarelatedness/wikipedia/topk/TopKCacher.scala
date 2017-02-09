package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk

import java.io.File
import java.util.concurrent.TimeUnit

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.logging.ProgressLogger
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import org.slf4j.LoggerFactory


/**
  * Class used to generate the cache over a sequence of WikiRelateTask for a topK method.
  *
  */
object TopKCacher {
  protected def logger = LoggerFactory.getLogger(getClass)


  /**
    * Generate the cache for a TopKCached method over tasks.
    *
    * @param tasks
    * @return
    */
  def generate(topK: TopKCached, tasks: Seq[WikiRelateTask], outpath: String) = {
    // get unique wikiIDs from tasks
    val wikiIDs = tasks.foldLeft(List.empty[Int])((IDs, task) => IDs ++ List(task.src.wikiID, task.dst.wikiID)).distinct

    val cache = new Int2ObjectOpenHashMap[Seq[Tuple2[Int, Float]]]()  // before it was List and not Seq...

    val pl = new ProgressLogger(logger, 1, TimeUnit.MINUTES)
    pl.start("Generating cache...")

    wikiIDs.foreach {
      case wikiID =>

        val scoredEntities = topK.topKScoredEntities(wikiID, 10000)
        cache.put(wikiID, scoredEntities)

        pl.update()
    }
    pl.done()

    logger.info("Serializing cache into %s".format(topK.getCachePath))
    //new File(topK.getCachePath()).getParentFile.mkdirs

    new File(topK.getCachePath).getParentFile.mkdirs
    BinIO.storeObject(cache, topK.getCachePath)
  }

}
