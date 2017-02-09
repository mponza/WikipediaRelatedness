package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.similarity.matrix

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph.WikiJungGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.similarity.SimRanker


abstract class MatrixSimilarity extends SimRanker {


  def similarity(srcWikiID: Int, dstWikiID: Int, wikiJungGraph: WikiJungGraph): Double

}