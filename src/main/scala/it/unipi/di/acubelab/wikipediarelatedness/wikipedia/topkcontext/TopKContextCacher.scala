package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topkcontext

import java.io.File
import java.util.concurrent.TimeUnit

import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import it.unimi.dsi.logging.ProgressLogger
import it.unipi.di.acubelab.wikipediarelatedness.dataset.{DatasetFactory, WikiRelateTask}
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topkcontext.neural.W2VTopKContext
import org.slf4j.LoggerFactory


object TopKContextCacher {
  protected def logger = LoggerFactory.getLogger(getClass)


  /**
    * Generate the cache for a TopKCached method over tasks.
    *
    * @param tasks
    * @return
    */
  def generate(topK: TopKContextCached, tasks: Seq[WikiRelateTask], outpath: String) = {
    // get unique wikiIDs from tasks
    val wikiPairs = tasks.map( task => ( task.src.wikiID, task.dst.wikiID ) ) // tasks.foldLeft(List.empty[Int])((IDs, task) => IDs ++ List(task.src.wikiID, task.dst.wikiID)).distinct

    val cache = new Long2ObjectOpenHashMap[Seq[Tuple2[Int, Float]]]()  // before it was List and not Seq...

    val pl = new ProgressLogger(logger, 1, TimeUnit.MINUTES)
    pl.start("Generating context cache...")

    wikiPairs.foreach {
      case (srcWikiID, dstWikiID) =>

        val srcShifted = srcWikiID.asInstanceOf[Long] << 32
        val cacheKey = srcShifted | dstWikiID

        val scoredEntities = topK.topKScoredEntities(srcWikiID, dstWikiID, 100)  // 10000
        cache.put(cacheKey, scoredEntities)

        pl.update()
    }
    pl.done()

    logger.info("Serializing cache into %s".format(topK.getCachePath))
    //new File(topK.getCachePath()).getParentFile.mkdirs

    new File(topK.getCachePath).getParentFile.mkdirs
    BinIO.storeObject(cache, topK.getCachePath)
  }

/*

  // run it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topkcontext.TopKContextCacher
  def main(args: Array[String]) {

    val topk = new W2VTopKContext(
      Config.getString("wikipedia.neural.w2v.corpus"),
      Config.getString("wikipedia.cache.topk.neural.corpus.context")
      //Config.getString("wikipedia.neural.deepwalk.dw10"),
      //Config.getString("wikipedia.cache.topk.neural.dw10.entity2entities")
    )

    val dataset = DatasetFactory.datasets()
    this.generate(topk, dataset.flatten, topk.getCachePath)
  }
*/
}
