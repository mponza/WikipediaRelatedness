package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms.ppr

import java.util.concurrent.TimeUnit

import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors
import edu.uci.ics.jung.graph.DirectedGraph
import it.unimi.dsi.logging
import it.unimi.dsi.logging.ProgressLogger
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph.WikiJungGraph
import org.apache.commons.collections15.Transformer
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._



class PersonalizedPageRank(val wikiJungGraph: WikiJungGraph, val wikiID: Int,
                            val pprDecay: Double, val iterations: Int) {

  protected val logger = LoggerFactory.getLogger(getClass)
  val priorVector = new StandardBasisPrior(wikiID)


  def computePPRVectors() : INDArray = {
    val ppr = new PPWP[java.lang.Integer, java.lang.Long](wikiJungGraph.graph, wikiJungGraph.weights, priorVector, pprDecay)
    ppr.setMaxIterations(iterations)
    ppr.setTolerance(0.0)

    //  (i, j) is the PPR score at i-th iteration of the node with wikiID j
    val pprVectors = Nd4j.zeros(4730474 + 1, iterations)

    val pl = new ProgressLogger(logger, 1, TimeUnit.SECONDS)
    pl.start("Computing PersonalizedPageRank...")

    for(i <- 0 until iterations) {

      ppr.step()

      logger.info("Stepped")
      logger.info(i.toString)

      // Updates the PageRank score for each node for each iteration.
      for (wikiID <- wikiJungGraph.graph.getVertices) {
        val pprScore = ppr.getVertexScore(wikiID)
        if (!pprScore.isNaN) pprVectors.put(i, wikiID, pprScore)
      }

      //logger.info("aaaaaaaaaaaaaaaaaaa")

      pl.update()

    }
    pl.done()

    pprVectors
  }

}




class PPWP[T, V](graph: DirectedGraph[T, V], w:  Transformer[V, java.lang.Double], prior: Transformer[T, java.lang.Double],
                 d: java.lang.Double) extends PageRankWithPriors[T, V](graph, w, prior, d) {

  override def step {
    super.swapOutputForCurrent
    import scala.collection.JavaConversions._

    println("Step")
    val pl = new logging.ProgressLogger(LoggerFactory.getLogger("Stepper"), 1, TimeUnit.SECONDS)
    pl.start("aaaaaaaaaaaaaaaaaaaStart")
    for (v <- this.graph.getVertices) {
      val diff: Double = update(v)
      updateMaxDelta(v, diff)

      pl.update()
    }

    pl.done()
    println("Iterated")
    total_iterations += 1
    afterStep
  }

}