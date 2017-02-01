package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.similarity
import java.util.concurrent.TimeUnit

import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors
import edu.uci.ics.jung.graph.DirectedGraph
import it.unimi.dsi.logging.ProgressLogger
import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms.ppr.StandardBasisPrior
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph.WikiJungGraph
import org.apache.commons.collections15.Transformer
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer


/**
  * Runs CoSimRank between two nodes of a graph with the specified parameters.
  *
  * @param iterations
  * @param pprDecay
  * @param csrDecay
  */
class CoSimRanker(val iterations: Int, val pprDecay: Double, val csrDecay: Double) extends SimRanker {

  protected val logger = LoggerFactory.getLogger(getClass)


  /**
    * Runs CoSimRank over wikiJungGraph between srcWikiID and dstWikiID
    *
    * @param wikiJungGraph
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  def similarity(wikiJungGraph: WikiJungGraph, srcWikiID: Int, dstWikiID: Int) = {



    val srcPPRVectors = pageRankVectors(srcWikiID, wikiJungGraph)
    val dstPPRVectors = pageRankVectors(dstWikiID, wikiJungGraph)

    logger.info("Cosine...")
    var sim = 0f
    for(i <- 0 until srcPPRVectors.size) {
      val srcVec = srcPPRVectors(i)
      val dstVec = dstPPRVectors(i)

      sim += Math.pow(csrDecay, i).toFloat * Similarity.cosineSimilarity(srcVec, dstVec)
    }

    logger.info("Relatedness between %d and %d is %1.5f".format(srcWikiID, dstWikiID, (1 - csrDecay) * sim))


    ((1 - csrDecay) * sim).toFloat
  }

  /**
    * Returns PPR
    *
    * @param wikiID
    * @return
    */
  protected def pageRanker(wikiID: Int, graph: DirectedGraph[Int, Long], weights: Transformer[Long, java.lang.Double]) = {
    val prior = new StandardBasisPrior(wikiID)
    val pr = new PageRankWithPriors[Int, Long](graph, weights, prior, pprDecay)
    //pr.setTolerance(0.0)
    pr.setMaxIterations(iterations)
    pr.setTolerance(0.0)

    pr
  }


  /**
    * Returns a list of [(wikiID, PPRScore)].
    *
    * @param ranker
    * @return
    */
  protected def getRankingVector(ranker: PageRankWithPriors[Int, Long], wikiJungGraph: WikiJungGraph) = {
    import scala.collection.JavaConversions._

    val vector = ListBuffer.empty[Tuple2[Int, Float]]

    for (wikiID <- wikiJungGraph.graph.getVertices) {
      val pprScore = ranker.getVertexScore(wikiID).toFloat

      if (pprScore.isNaN) {
        vector += Tuple2(wikiID, 0f)
      } else {
        vector += Tuple2(wikiID, pprScore)
      }
    }

    vector.toList
  }


  protected def pageRankVectors(wikiID: Int, wikiJungGraph: WikiJungGraph) = {
    val ranker = pageRanker(wikiID, wikiJungGraph.graph, wikiJungGraph.weights)

    val pprVectors = ListBuffer.empty[List[Tuple2[Int, Float]]]

    val pl = new ProgressLogger(logger, 1, TimeUnit.SECONDS)
    pl.start("Computing PersonalizedPageRank...")
    for(i <- 0 until iterations) {
      ranker.step()
      pprVectors += getRankingVector(ranker, wikiJungGraph)

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
  def similarity(wikiJungGraph: WikiJungGraph, srcWikiID: Int, dstWikiID: Int) = {
    val srcPPRVectors = pageRankVectors(srcWikiID, wikiJungGraph)
    val dstPPRVectors = pageRankVectors(dstWikiID, wikiJungGraph)

    logger.info("Cosine...")
    var sim = 0f
    for(i <- 0 until srcPPRVectors.size) {
      val srcVec = srcPPRVectors(i)
      val dstVec = dstPPRVectors(i)

      sim += Math.pow(csrDecay, i).toFloat * Similarity.cosineSimilarity(srcVec, dstVec)
    }

    logger.info("Relatedness between %d and %d is %1.5f".format(srcWikiID, dstWikiID, (1 - csrDecay) * sim))


    ((1 - csrDecay) * sim).toFloat
  }
}


/*
*
*
* package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms

import java.util.concurrent.TimeUnit

import it.unimi.dsi.logging.ProgressLogger
import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms.ppr.JungPPRSimilarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.oldgraph.JungWikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

class JungCoSimRank(junkWikiGraph: JungWikiGraph, relatedness: Relatedness,
                    iterations: Int = 30, pprDecay: Float = 0.8f, val csrDecay: Float = 0.8f)
  extends JungPPRSimilarity(junkWikiGraph, relatedness, iterations, pprDecay)
{
  override def logger = LoggerFactory.getLogger(classOf[JungCoSimRank])


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
*
* */
