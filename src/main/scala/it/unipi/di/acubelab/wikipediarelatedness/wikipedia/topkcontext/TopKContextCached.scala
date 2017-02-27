package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topkcontext

import java.io.File

import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import org.slf4j.Logger


/**
  * Class which manages the context between two Wikipedia enitities to retrieve the top-k related entities.
  *
  */
trait TopKContextCached extends TopKContext {
  protected def logger: Logger
  protected def cacheTopKContextPath: String

  protected val cacheTopKContext= loadCache()


  /**
    * Loads topK entities2entities cache.
    *
    * @return
    */
  protected def loadCache() : Long2ObjectOpenHashMap[Seq[(Int, Float)]] = {
    if ( !new File(cacheTopKContextPath).exists() ) {
      logger.warn("Cache not found. No cache will be used.")
      return new Long2ObjectOpenHashMap[Seq[(Int, Float)]]()
    }

    logger.info("Loading cache from %s...".format(cacheTopKContextPath))
    val cache = BinIO.loadObject(cacheTopKContextPath).asInstanceOf[Long2ObjectOpenHashMap[Seq[Tuple2[Int, Float]]]]
    logger.info("Cache loaded!")

    cache
  }


  def topKScoredEntities(srcWikiID: Int, dstWikiID: Int, k: Int): Seq[(Int, Float)] = {
    val cacheKey = getCacheKey(srcWikiID, dstWikiID)

    if (cacheTopKContext.containsKey(cacheKey)) {

      cacheTopKContext.get(cacheKey).slice(0, k)

    } else {
      logger.debug("%d %d context not in cache.".format(srcWikiID, dstWikiID))
      nonCachedTopKScoredEntities(srcWikiID, dstWikiID, k)
    }
  }


  /**
    * The hash-table input key is a Long.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  protected def getCacheKey(srcWikiID: Int, dstWikiID: Int) = {
    val srcShifted = srcWikiID.asInstanceOf[Long] << 32
    srcShifted | dstWikiID
  }


  /**
    * Pure computation of the top-k most similar entities wihtout using cache.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @param k
    * @return
    */
  protected def nonCachedTopKScoredEntities(srcWikiID: Int, dstWikiID: Int, k: Int) : Seq[Tuple2[Int, Float]]


  def getCachePath: String = cacheTopKContextPath
}