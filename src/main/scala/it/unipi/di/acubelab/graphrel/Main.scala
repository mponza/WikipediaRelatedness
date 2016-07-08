package it.unipi.di.acubelab.graphrel

import it.unipi.di.acubelab.graphrel.wikipedia.processing.graph.BVGraphProcessing


object Run {
  def main(args: Array[String]) {
    val bvGraphProcessing = new BVGraphProcessing
    bvGraphProcessing.processWikiGraph

  }
}