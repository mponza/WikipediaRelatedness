package it.unipi.di.acubelab.graphrel

import it.unipi.di.acubelab.graphrel.dataset.WikiSimDataset
import it.unipi.di.acubelab.graphrel.wikipedia.{ImmutableWikiGraph, WikiGraph}


object Run {
  def main(args: Array[String]) {
    val wikiSimReader = new WikiSimDataset()
    println("Loaded!")
    println(wikiSimReader.wikiSimPairs.length)

    val wikiGraph = new ImmutableWikiGraph()


  }
}