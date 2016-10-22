package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph

import it.unimi.dsi.fastutil.doubles.{DoubleArrayList, DoubleList}
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.law.rank.{PageRankParallelGaussSeidel, SpectralRanking}
import it.unimi.dsi.webgraph.Transform
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.WikiGraph
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import it.unimi.dsi.webgraph.ImmutableGraph
import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity

/**
  * CoSimRank algorithm for the Wikipedia graph.
  * @param wikiBVGraph Wikipedia Graph, stored via WebGraph.
  * @param iterations number of PPR vectors by CoSimRank

  * @param pprDecay PageRank weight decay (d in the paper)
  * @param csrDecay CoSimRank weight decay (c in the paper)
  *
  */
class CoSimRank(val wikiBVGraph: WikiBVGraph = WikiGraph.outGraph, val iterations: Int = 30,
                 val pprDecay: Float = 0.8f, val csrDecay: Float = 0.8f) {

  val logger = LoggerFactory.getLogger(classOf[CoSimRank])

  /**
    * Computes CoSimRank similarity score between two Wikipedia nodes.
    * @param srcWikiID
    * @param dstWikiID
    */
  def similarity(srcWikiID: Int, dstWikiID: Int) : Float = {
    val srcPPRvectors = computePPRVectors(srcWikiID)
    val dstPPRvectors = computePPRVectors(dstWikiID)

    var csrSimilarity = 0.0
    for(i <- 0 until iterations) {
      val srcPPRvector = srcPPRvectors.get(i)
      val dstPPRvector = dstPPRvectors.get(i)

      csrSimilarity += math.pow(csrDecay, i) * Similarity.cosineSimilarity(srcPPRvector, dstPPRvector)//dotProduct(srcPPRvector, dstPPRvector)
    }

    csrSimilarity.toFloat
  }

  def dotProduct(src: DoubleArrayList, dst: DoubleArrayList) : Double = {
    if (src.size() != dst.size()) throw new IllegalArgumentException("Dot product error. Lists have different size.")

    var dot = 0.0
    for(i <- 0 until src.size()) {
      dot += src.getDouble(i) * dst.getDouble(i)
    }

    println("DOT %1.5f".format(dot))
    dot
  }

  /**
    * Runs PersonalizedPageRank on the Wikipedia graph by drugging the preference vector of wikiID.
    * @return List of PPRVectors, where the i-th vector is the PPR distribution at i-th iteration.
    */
  def computePPRVectors(wikiID: Int): ObjectArrayList[DoubleArrayList] = {
    val pageRanker = new PageRankParallelGaussSeidel(Transform.transpose(wikiBVGraph.bvGraph))


    // Configuration of PPR parameters.
    pageRanker.alpha = pprDecay.toDouble
    // Vector of 0.0 with 1.0 in wikiID.
    pageRanker.preference = preferenceVector(wikiID)

    val pprVectors = new ObjectArrayList[DoubleArrayList]()

    // At each PPR iteration we save the PPR distribution.
    for (i <- 0 until iterations) {
      pageRanker.stepUntil(new SpectralRanking.IterationNumberStoppingCriterion(i))

      val pprVector = new DoubleArrayList(pageRanker.rank)
      pprVectors.add(pprVector)

      if(i >= 1 && checkVectors(pprVectors.get(i - 1), pprVectors.get(i))) {
        println("Equal vectors WTF")
      }
    }
    pprVectors
  }

  def checkVectors(old: DoubleArrayList, n: DoubleArrayList) : Boolean = {
    for(i <- 0 until old.size()) {
      if(old.getDouble(i) != n.getDouble(i)) {
        return false
      }
    }

    true
  }

  /**
    * Builds the preference vector of a given WikipediaID.
    */
  def preferenceVector(wikiID: Int) : DoubleList = {
    val preference = Array.fill[Double](wikiBVGraph.bvGraph.numNodes())(0.0)

    val nodeID = WikiBVGraph.getNodeID(wikiID)
    preference(nodeID) = 1.0

    new DoubleArrayList(preference)
  }
}
