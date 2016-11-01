package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.pagerank.wikiwalk

import it.unimi.dsi.fastutil.doubles.{DoubleArrayList, DoubleList}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa.ESA
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa.lemma.LemmaLuceneIndex
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.algorithms.pagerank.pprcos.PPRCos
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.{WikiGraph, WikiGraphFactory}
import org.slf4j.LoggerFactory

/**
  * PPRCos with a preference vector built by using the semantic interpreter of ESA.
  * @param wikiGraph
  * @param iterations
  * @param pprDecay
  */
class WikiWalk(wikiGraph: WikiGraph = WikiGraphFactory.outGraph, iterations: Int = 30, pprDecay: Float = 0.8f)
  extends PPRCos(wikiGraph, iterations, pprDecay) {

  protected val esa = new LemmaLuceneIndex()

  override def getLogger() = LoggerFactory.getLogger(classOf[WikiWalk])

  /**
    * Builds the preference vector of a given WikipediaID by using as preferences the normalized ESA vector.
    */
  override def preferenceVector(wikiID: Int) : DoubleList = {
    val esaVector = ESA.wikipediaConcepts(wikiID)
    val preference = Array.fill[Double](wikiGraph.graph.numNodes())(0.0)

    // L1-norm
    val normFactor = esaVector.foldLeft(0f)(_ + _._2)

    // set preference entries with a normalized esa score
    ESA.wikipediaConcepts(wikiID).foreach {
      case (wikiID, score) =>
        val nodeID = wikiGraph.getNodeID(wikiID)
        preference(nodeID) = score / normFactor
    }

    new DoubleArrayList(preference)
  }

}
