package it.unipi.di.acubelab.wikipediarelatedness.options

import it.unipi.di.acubelab.wikipediarelatedness.utils.ArithmeticFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessFactory

class KShortestPathsOptions(json: Option[Any] = None) extends RelatednessOptions(json) {
  // technique for subgraph generation
  val subGraph = getString("subGraph", "esa")
  // number of nodes in the subgraph
  val threshold = getInt("threshold", 1000)
  // how weight subgraph nodes
  val weighting = RelatednessFactory.make(getString("weighting"))

  // number of shortest paths
  val k = getInt("k", 10)
  // function to map path weights upon a number
  val pathFun = getString("pathFun", "avg")
  // function to map the number of each path to a unique one
  val kFun = getString("kFun", "avg")
  // function to combine two k-shortest path from src to dst
  val combFun = getString("combFun", "avg")


  override def toString(): String = {
    "subGraph:%s,weighting:%s,threshold:%d,k:%d,p:%s,kf:%s,c:%s".formatLocal(java.util.Locale.US,
      subGraph,
      weighting,
      threshold,
      k, pathFun, kFun, combFun
    )
  }
}