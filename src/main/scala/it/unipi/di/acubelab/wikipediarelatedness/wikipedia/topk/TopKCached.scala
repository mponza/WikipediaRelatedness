package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk

import java.io.File

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.io.BinIO
import org.slf4j.Logger


/**
  * Class with a pre-computed set of top-k (wikiID, weight) for each entity.
  *
  */
trait TopKCached extends TopK {
  protected def logger: Logger
  protected def cachePath: String

  protected val cache = loadCache()  // Entity2ScoredEntities cache


  /**
    * Loads topK entity2entities cache.
    *
    * @return
    */
  protected def loadCache() : Int2ObjectOpenHashMap[List[(Int, Float)]] = {
    if ( !new File(cachePath).exists() ) {
      logger.warn("Cache not found. No cache will be used.")
      return new Int2ObjectOpenHashMap[List[(Int, Float)]]()
    }

    logger.info("Loading cache from %s...".format(cachePath))
    val cache = BinIO.loadObject(cachePath).asInstanceOf[Int2ObjectOpenHashMap[List[Tuple2[Int, Float]]]]
    logger.info("Cache loaded!")

    cache
  }


  /**
    * Returns topK entities with their weights.
    *
    * @param wikiID
    * @param k
    */
  override def topKScoredEntities(wikiID: Int, k: Int): Seq[Tuple2[Int, Float]] = {
    if (cache.containsKey(wikiID)) {
      cachedTopKScoredEntities(wikiID, k)

    } else {
      logger.debug("%d not in cache.".format(wikiID))
      nonCachedTopKScoredEntities(wikiID, k)
    }
  }


  /**
    * Uses cache to retrieve top-k most similar entities to wikiID.
    *
    * @param wikiID
    * @param k
    * @return
    */
  protected def cachedTopKScoredEntities(wikiID: Int, k: Int) = cache.get(wikiID).slice(0, k)


  /**
    * Pure computation of the top-k most similar entities wihtout using cache.
    *
    * @param wikiID
    * @param k
    * @return
    */
  protected def nonCachedTopKScoredEntities(wikiID: Int, k: Int) : Seq[Tuple2[Int, Float]]


  /**
    * Path to the cache.
    *
    * @return
    */
  def getCachePath() = cachePath
}
