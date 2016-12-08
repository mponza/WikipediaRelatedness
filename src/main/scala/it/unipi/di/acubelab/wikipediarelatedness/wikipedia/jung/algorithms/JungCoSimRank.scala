package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms

import java.util.concurrent.TimeUnit

import it.unimi.dsi.logging.ProgressLogger
import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph.JungWikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

class JungCoSimRank(junkWikiGraph: JungWikiGraph, relatedness: Relatedness,
                    iterations: Int = 30, pprDecay: Float = 0.8f, val csrDecay: Float = 0.8f)
  extends JungPPRSimilarity(junkWikiGraph, relatedness, iterations, pprDecay)
{

  override def logger = LoggerFactory.getLogger(classOf[JungCoSimRank])


  /**
    * For each iteration
    * @param wikiID
    * @return
    */
  override protected def pageRankVectors(wikiID: Int) = {
    val ranker = pageRanker(wikiID)

    val pprVectors = ListBuffer.empty[List[Tuple2[Int, Float]]]

    val pl = new ProgressLogger(logger, 1, TimeUnit.SECONDS)
    pl.start("Computing PersonalizedPageRank...")
    for(i <- 0 until iterations) {
      ranker.step()
      pprVectors += getRankingVector(ranker)

      pl.update()
    }
    pl.done()

    pprVectors.toList
  }


  /**
    * Similarity via CoSimRank.
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  override def similarity(srcWikiID: Int, dstWikiID: Int) = {
    val srcPPRVectors = pageRankVectors(srcWikiID)
    val dstPPRVectors = pageRankVectors(dstWikiID)

    logger.info("Cosine...")
    var sim = 0f
    for(i <- 0 until srcPPRVectors.size) {
      val srcVec = srcPPRVectors(i)
      val dstVec = dstPPRVectors(i)

      sim += Math.pow(csrDecay, i).toFloat * Similarity.cosineSimilarity(srcVec, dstVec)
    }

    logger.info("Relatedness between %d and %d is %1.5f".format(srcWikiID, dstWikiID, (1 - csrDecay) * sim))


    (1 - csrDecay) * sim
  }

}