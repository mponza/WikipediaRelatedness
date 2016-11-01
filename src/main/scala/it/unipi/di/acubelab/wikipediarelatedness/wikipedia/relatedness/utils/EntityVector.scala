package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.utils

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa.ESA
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.{WikiGraph, WikiGraphFactory}

/**
  * Maps a wikiID into an array of wikiIDs by using the specified heuristics (i.e. graph neighbors or ESA).
  */
object EntityVector {

  def make(wikiID: Int, vectorizer: String) : Array[Int] = vectorizer match {
    // Wikipedia Graph
    case "inGraph" => getGraphNeighborhs(wikiID, WikiGraphFactory.inGraph)
    case "outGraph" => getGraphNeighborhs(wikiID, WikiGraphFactory.outGraph)
    case "noLoopSymGraph" => getGraphNeighborhs(wikiID, WikiGraphFactory.noLoopSymGraph)

    // ESA
    case "ESA" | "esa" => getESAWikiIDs(wikiID)

    case _ => throw new IllegalArgumentException("Vectorizer %s does not exist.".format(vectorizer))
  }


  /**
    *
    * @param wikiGraph
    * @param wikiID
    * @return Array of wikiIDs which are neighborhs of wikiID in wikiGraph.
    */
  protected def getGraphNeighborhs(wikiID: Int, wikiGraph: WikiGraph) = {
    wikiGraph.successorArray(wikiID).map(wikiGraph.getWikiID(_)).sorted
  }


  /**
    *
    * @param wikiID
    * @return Array of wikiIDs top-scored by ESA.
    */
  protected def getESAWikiIDs(wikiID: Int) : Array[Int] = {
    ESA.wikipediaConcepts(wikiID).map(_._1).toArray
  }
}