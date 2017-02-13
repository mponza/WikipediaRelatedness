package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.ranker.{GaussSeidelPageRanker, PowerPageRanker, PushPriorRanker}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.ops.transforms.Transforms
import org.slf4j.LoggerFactory


class PPRRanker(val iterations: Int, val alpha: Float) {
  protected val logger = LoggerFactory.getLogger(getClass)

  protected val (srcWikiBVGraph, dstWikiBVGraph) = (WikiBVGraphFactory.make("in"), WikiBVGraphFactory.make("in"))
  protected var score = 0.0

  protected def initSimilarityScore() = score = 0.0


  def similarity(srcWikiID: Int, dstWikiID: Int) = {
    initSimilarityScore()

    val srcPPR = new PushPriorRanker( srcWikiID, srcWikiBVGraph, alpha )
    val dstPPR = new PushPriorRanker( dstWikiID, dstWikiBVGraph, alpha )

    val srcRanks = srcPPR.rankingSteps(iterations)
    val dstRanks = dstPPR.rankingSteps(iterations)

    val srcVec = Nd4j.create(srcRanks)
    val dstVec = Nd4j.create(dstRanks)

    Transforms.cosineSim(srcVec, dstVec).toFloat
  }


  def updateSimilarityScore(src: Array[Double], dst: Array[Double], iteration: Int) = {
    if (iteration == iterations) {
      val srcVec = Nd4j.create(src)
      val dstVec = Nd4j.create(dst)

      score = Transforms.cosineSim(srcVec, dstVec).toFloat
    }
  }

}


// Ingegnerizzarlo bene in modo e usare come stopping criterion di push quello sulla norma, quello con le iterazioni non sembrano essere supportate