package it.unipi.di.acubelab.wikipediarelatedness.options.Set

import it.unipi.di.acubelab.wikipediarelatedness.options.RelatednessOptions


/**
  * Configuration settings for MilneWitten, Jaccard and LocalClustering
  * relatedness algorithms, namely the graph to be used (in/out/sym).
  *
  */
case class SetOptions(graph: String = "in") extends RelatednessOptions {}


/**
  * Factory object to build SetOptions from string.
  *
  */
object SetOptions {

  /**
    * Generates a MilneWittenOptions from a raw args.
    *
    *   "-graph [in, out, sym]"
    *
    * @param args
    */
  def parseString(args: Array[String]) = {

  }
}
