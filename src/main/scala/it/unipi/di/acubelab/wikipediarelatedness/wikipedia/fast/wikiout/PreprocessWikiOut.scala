package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast.wikiout

import java.io.File
import java.util.concurrent.TimeUnit

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap
import it.unimi.dsi.logging.ProgressLogger
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{RelatednessFactory, RelatednessOptions}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.TopKFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory


/**
  * Class used to preprocess the Wikipedia out information, i.e. MW-TopK from the out-neighborhood and
  * their MWDW relatedness.
  *
  */
object PreprocessWikiOut {
  protected val logger = LoggerFactory.getLogger("PreprocessWikiOut")

  def main(args: Array[String]) {
    val wikiID2TopKOut = new Int2ObjectOpenHashMap[ Array[Int] ]()
    val wikiIDs2MWDW = new Long2FloatOpenHashMap()

    val wikiBVGraph = WikiBVGraphFactory.make("out")
    val mwTopK = TopKFactory.make("mw.out")

    val options = new RelatednessOptions(name="mix", lambda=0.5,
      firstname="milnewitten", firstgraph="in",
      secondname="w2v", secondmodel="deepwalk.dw10"
    )
    val mwdwRel = RelatednessFactory.make(options)

    val pl = new ProgressLogger(logger, 1, TimeUnit.MINUTES)
    pl.start("Pre-processing scored out nodes for each Wikipedia Entity...")

    val wikiOut = new WikiOut

    for(wikiID <- wikiBVGraph.getVertices) {
      //val topKouts =  mwTopK.topKEntities(wikiID, 30)

      //wikiID2TopKOut.put(wikiID, topKouts.toArray)

      val topKouts = wikiOut.topK(wikiID)

      topKouts.foreach {
        case wID =>
          val rel = mwdwRel.computeRelatedness(wikiID, wID)

          // Hash key
          val src = Math.min(wikiID, wID)
          val dst = Math.max(wikiID, wID)
          val srcShifted = src.asInstanceOf[Long] << 32
          val wikiIDsKey = srcShifted | dst

          wikiIDs2MWDW.put( wikiIDsKey, rel)
      }

      pl.update()
    }
    pl.done()

    //logger.info("Storing TopK...")
    //val topkoutfile = Config.getString("wikipedia.cache.fast.wikiout.topk")
    //new File(topkoutfile).getParentFile.mkdirs()
    //BinIO.storeObject(wikiID2TopKOut, topkoutfile)

    logger.info("Storing MWDW...")
    val mwdwoutfile = Config.getString("wikipedia.cache.fast.wikiout.mwdw")
    BinIO.storeObject(wikiIDs2MWDW, mwdwoutfile)

    logger.info("Storing done.")
  }
}

