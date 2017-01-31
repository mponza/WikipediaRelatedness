package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph

import edu.uci.ics.jung.graph.DirectedGraph
import org.apache.commons.collections15.Transformer


/**
  * Directed Jung Graph of Wikipedia Entities, properly weighted.
  *
  */
trait WikiJungGraph {

  def graph:  DirectedGraph[Int, Long]
  def weights: Transformer[Long, java.lang.Double]

}


