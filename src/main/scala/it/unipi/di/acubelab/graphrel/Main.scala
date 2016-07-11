package it.unipi.di.acubelab.graphrel

import it.unimi.dsi.webgraph.BVGraph
import it.unipi.di.acubelab.graphrel.utils.Configuration
import it.unipi.di.acubelab.graphrel.wikipedia.processing.webgraph.WebGraphProcessor


object WebGraph {
  def main(args: Array[String]) {
    val bvGraphProcessing = new WebGraphProcessor

    // Creates and stores BVGraph from the raw Wikipedia graph.
    bvGraphProcessing.generateBVGraph

    val out = BVGraph.load(Configuration.wikipedia.outBVGraph)

    println(out.toString)
    //println(in.toString)
  }
}