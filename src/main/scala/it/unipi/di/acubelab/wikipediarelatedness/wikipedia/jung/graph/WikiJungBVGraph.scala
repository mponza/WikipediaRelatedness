package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph
/*

import edu.uci.ics.jung.graph.DirectedSparseGraph
import it.unimi.dsi.webgraph.jung.JungAdapter
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.weight.UniformWeights
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.{WikiBVGraph, WikiBVGraphFactory}




class WikiJungBVGraph extends WikiJungGraph {
  override val graph = new JungWikiAdapter(
                                WikiBVGraphFactory.make("out", true),  // what if false? what if non compressed?
                                WikiBVGraphFactory.make("in", true)
                              )

  override def weights = new UniformWeights(graph)
}


class JungWikiAdapter(graph: WikiBVGraph, transposedGraph: WikiBVGraph) extends JungAdapter(graph, transposedGraph) {

  override def getVertices = {
    import scala.collection.JavaConversions.asJavaCollection
    asJavaCollection(graph.getVertices.map(new Integer(_)))
  }
}*/