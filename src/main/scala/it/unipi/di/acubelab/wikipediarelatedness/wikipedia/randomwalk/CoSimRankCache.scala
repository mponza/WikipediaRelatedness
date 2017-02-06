package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.randomwalk

import java.io.File
import java.util.concurrent.TimeUnit

import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap
import it.unimi.dsi.logging.ProgressLogger
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.service.CoSimRankServer
import org.slf4j.LoggerFactory


/**
  * Cache of pre-computed cache by running CoSimRank and PPR+Cos over the whole Wikipedia graph.
  *
  * @param decay
  * @param iterations
  */
class CoSimRankCache(val decay: Float, val iterations: Int) {
  protected val logger = LoggerFactory.getLogger(getClass)

  protected val cosimrank = loadCache( Config.getString("wikipedia.cache.cosimrank.csr") )
  protected val pprcos = loadCache( Config.getString("wikipedia.cache.cosimrank.ppr") )


  /**
    * Given a directory, returns the coressponding deserialized hash.
    *
    * @param dir
    * @return
    */
  protected def loadCache(dir: String) : Object2FloatOpenHashMap[(Int, Int)]= {
    val path =  new File(dir, "decay:%1.2f_iterations:%d".format(decay, iterations) ).toString  // cache path

    if ( !new File(path).exists() ) {
      logger.warn("Cache in %s with parameters decay: %1.2f and iterations: %d not found."
                .format(dir, decay, iterations))
      return new Object2FloatOpenHashMap[(Int, Int)]()
    }

    BinIO.loadObject(path).asInstanceOf[Object2FloatOpenHashMap[(Int, Int)]]
  }


  /**
    * Returns cached CoSimRank similarity. -1 if not present.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  def getCoSimRank(srcWikiID: Int, dstWikiID: Int) : Float = cosimrank.getOrDefault( Tuple2(srcWikiID, dstWikiID) ,
                                                                                      -1.0f )


  /**
    * Returns cached PPR+Cos similarity. -1 if not present.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  def getPPRCos(srcWikiID: Int, dstWikiID: Int) = pprcos.getOrDefault( Tuple2(srcWikiID, dstWikiID) , -1.0f )

}



object CoSimRankCache {
  protected val logger = LoggerFactory.getLogger(getClass)


  /**
    * Generates CoSimRank and PPR+Cos cache over dataset with the specified number of iterations and weight decay.
    *
    * @param dataset
    * @param iterations
    * @param decay
    */
  def generateCache(dataset: Seq[WikiRelateTask], iterations: Int, decay: Float) = {

    val csr = new Object2FloatOpenHashMap[(Int, Int)]()
    val ppr = new Object2FloatOpenHashMap[(Int, Int)]()

    val pl = new ProgressLogger(logger, 1, TimeUnit.MINUTES)
    pl.start("Generating caches...")

    for(task <- dataset) {
      val src = task.src.wikiID
      val dst = task.dst.wikiID

      CoSimRankServer.similarities(src, dst, iterations, decay) match {
        case (csrSim, pprSim) =>
          csr.put( Tuple2(src, dst), csrSim)
          csr.put( Tuple2(dst, src), csrSim)

          ppr.put( Tuple2(src, dst), pprSim)
          ppr.put( Tuple2(dst, src), pprSim)

          logger.info("CoSimRank Similarity between %d and %d is %1.5f".format(src, dst, csrSim))
          logger.info("PPR+Cos Similarity between %d and %d is %1.5f".format(src, dst, pprSim))

          pl.update()
      }
    }

    pl.done()


    logger.info("Serializing CoSimRank cache...")
    val csrPath = new File( Config.getString("wikipedia.cache.cosimrank.csr") , "decay:%1.2f_iterations:%d".format(decay, iterations) ).toString

    new File(csrPath).getParentFile.mkdirs
    BinIO.storeObject( csr, csrPath )


    logger.info("Serializing PPR+Cos cache...")
    val pprPath = new File( Config.getString("wikipedia.cache.cosimrank.ppr") , "decay:%1.2f_iterations:%d".format(decay, iterations) ).toString

    new File(pprPath).getParentFile.mkdirs
    BinIO.storeObject( csr, pprPath )
  }

}