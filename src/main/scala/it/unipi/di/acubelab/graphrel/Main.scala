package it.unipi.di.acubelab.graphrel

import it.unipi.di.acubelab.graphrel.wikisim.WikiSimDataset


object Run {
  def main(args: Array[String]) {
    val wikiSimReader = new WikiSimDataset
    println(args)
  }
}