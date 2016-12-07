package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms

import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph.JungWikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness

import scala.collection.mutable.ListBuffer

class JungCoSimRank(junkWikiGraph: JungWikiGraph, relatedness: Relatedness,
                    iterations: Int = 30, pprDecay: Float = 0.8f, val csrDecay: Float = 0.8f)
  extends JungPPRSimilarity(junkWikiGraph, relatedness, iterations, pprDecay)
{

  override protected def pageRankVectors(wikiID: Int) = {
    def ranker = pageRanker(wikiID)

    val pprVectors = ListBuffer.empty[List[Tuple2[Int, Float]]]

    for(i <- 0 until iterations) {
      ranker.step()
      pprVectors += getRankingVector(ranker)
    }

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

    var sim = 0f
    for(i <- 0 until iterations) {
      val srcVec = srcPPRVectors(i)
      val dstVec = dstPPRVectors(i)

      srcVec.map(_._1).zip(dstVec.map(_._1)).foreach {
        case (s, d) => assert(s == d)
      }

      sim += Math.pow(csrDecay, i) * Similarity.cosineSimilarity(srcVec, dstVec)
    }

    (1 - csrDecay) * sim
  }
}
