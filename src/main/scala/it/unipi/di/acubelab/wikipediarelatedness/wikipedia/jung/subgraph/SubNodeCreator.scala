package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph.topk.ESASubNodeCreator

/**
  * Generates a subset of nodes by using a specified heuristics.
  *
  */
trait SubNodeCreator {


  /**
    * Returns the a Seq of wikiIDs given two wikiIDs.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  def subNodes(srcWikiID: Int, dstWikiID: Int) : Seq[Int]
}


object SubNodeCreatorFactory {

  def make(name: String, size: Int) = name match {
    case "esa" => new ESASubNodeCreator(size)
  }
}