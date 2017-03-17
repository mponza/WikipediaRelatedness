package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast

import java.io.File
import java.util.concurrent.TimeUnit

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.logging.ProgressLogger
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{RelatednessFactory, RelatednessOptions}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.TopKFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory


/**
  * Class for computing and pre-processing the sorted out neighborhood for each entity
  *
  */
object OutNeighborhood {
  protected val logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]) {
    // 1. TopK nodes of wikiID computed with mw.out,
    // 2. weighted with Milne&Witten+DW-cbow and
    // 3. finally sorted by their ID.
    val wikiID2ScoredOut = new Int2ObjectOpenHashMap[Array[(Int, Float)]]() // wikiID -> [(int, float)]

    val wikiBVGraph = WikiBVGraphFactory.make("out")
    val mwTopK = TopKFactory.make("mw.out")

    val options = new RelatednessOptions(name="mix", lambda=0.5,
                                       firstname="milnewitten", firstgraph="in",
                                       secondname="w2v", secondmodel="deepwalk.dw10"
                                      )
    val mwdwRel = RelatednessFactory.make(options)

    val pl = new ProgressLogger(logger, 1, TimeUnit.MINUTES)
    pl.start("Pre-processing scored out nodes for each Wikipedia Entity...")
    for(wikiID <- wikiBVGraph.getVertices) {
      val topKouts =  mwTopK.topKEntities(wikiID, 30)

      // [(int, float)]
      // Add node itself?
      val scoredOuts = topKouts.map(out => Tuple2(out, mwdwRel.computeRelatedness(wikiID, out)) ).toArray.sortBy(_._1)
      wikiID2ScoredOut.put(wikiID, scoredOuts)

      pl.lightUpdate()
    }
    pl.done()


    val outfile = Config.getString("wikipedia.cache.fast.scoredout")
    new File(outfile).getParentFile.mkdirs()

    logger.info("Storing scored out into %s..." format outfile)
    BinIO.storeObject(wikiID2ScoredOut, outfile)
    logger.info("Stored completed.")
  }
}
