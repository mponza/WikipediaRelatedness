package it.unipi.di.acubelab.graphrel

import it.unipi.di.acubelab.graphrel.dataset.WikiSimDataset


object Run {
  def main(args: Array[String]) {
    val wikiSimReader = new WikiSimDataset()
    println("Loaded!")
    println(wikiSimReader.wikiSimPairs.length)
  }
}