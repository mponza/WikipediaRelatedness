package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.secondstage.weights.cache

import java.io.File

import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap
import it.unimi.dsi.logging.ProgressLogger
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.graph.WikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.secondstage.weights.WeightsOfSubGraph
import org.slf4j.LoggerFactory


class CachedWeightsOfSubGraph(cacheFilename: String, weightsOfSubGraph: WeightsOfSubGraph) extends WeightsOfSubGraph {
  private val logger = LoggerFactory.getLogger(classOf[CachedWeightsOfSubGraph])
  private val wikiIDs2Weight = loadCache(cacheFilename)

  private def loadCache(cacheFilename: String) = {
    logger.info("Loading cache...")
    val cache = BinIO.loadObject(cacheFilename).asInstanceOf[ Long2FloatOpenHashMap ]
    logger.info(s"Done. Cache loaded with ${cache.keySet().size()} precomputed weights.")
    cache
  }


  private def hashMapKey(srcWikiID: Int, dstWikiID: Int) : Long = {
    ( (srcWikiID min dstWikiID).toLong << 32) | ((srcWikiID max dstWikiID) & 0XFFFFFFFFL)
  }


  override def weighting(srcWikiID: Int, dstWikiID: Int): Float = {
    val key = hashMapKey(srcWikiID, dstWikiID)
    if(wikiIDs2Weight.containsKey(key)) return wikiIDs2Weight.get(key)
    weightsOfSubGraph.weighting(srcWikiID, dstWikiID)
  }

}


object CachedWeightsOfSubGraph {
  private val logger = LoggerFactory.getLogger(classOf[CachedWeightsOfSubGraph])


  /**
    * Pre-compute all weights of weightsOfSubGraph between all linked pairs in the wikiGraph.
    * Use symmetric graph for computing as much pairs as possible. With limited memory out or in graph can be also ok.
    *
    * Warning this cache suppose symmetric weighting relation of weightsOfSubGraph, e.g. weight(src, dst) == weight(dst, src)
 *
    * @param weightsOfSubGraph
    * @param cacheFilename
    */
  def generateWeightsCache(wikiGraph: WikiGraph, weightsOfSubGraph: WeightsOfSubGraph, cacheFilename: String) = {
    val wikiIDs2Weight = new Long2FloatOpenHashMap()


    def hashMapKey(srcWikiID: Int, dstWikiID: Int) : Long = {
      ( (srcWikiID min dstWikiID).toLong << 32) | ((srcWikiID max dstWikiID) & 0XFFFFFFFFL)
    }

    val pl = new ProgressLogger(logger)
    pl.start("Starting cache generation...")

    wikiGraph.allIterableEdges()
      .foreach(edge => {

        val key = hashMapKey(edge._1, edge._2)
        if(!wikiIDs2Weight.containsKey(key)) {
          val rel = weightsOfSubGraph.weighting(edge._1, edge._2)
          // it's actually very sparse, maybe zeroes can be not saved
          wikiIDs2Weight.put(key, rel)
        }
        pl.update()

    })

    pl.done()


    logger.info("Serializing computed cache...")
    new File(cacheFilename).getParentFile.mkdirs()
    BinIO.storeObject(wikiIDs2Weight, cacheFilename)
    logger.info("Done.")

  }
}