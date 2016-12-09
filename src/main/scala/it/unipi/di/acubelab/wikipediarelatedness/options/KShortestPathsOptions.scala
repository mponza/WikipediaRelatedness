package it.unipi.di.acubelab.wikipediarelatedness.options

import it.unipi.di.acubelab.wikipediarelatedness.utils.ArithmeticFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessFactory

class KShortestPathsOptions(json: Option[Any] = None) extends CoSimRankOptions(json) {
  // technique for subgraph generation
  val subGraph = getString("subGraph", "esa")
  // number of nodes in the subgraph
  val threshold = getInt("threshold", 1000)
  // how weight subgraph nodes
  val weighting = RelatednessFactory.make(getString("weighting"))

  // number of shortest paths
  val k = getInt("k", 10)
  // function to map path weights upon a number
  val pathFun = ArithmeticFactory.make(getString("pathFun", "avg"))
  // function to map the number of each path to a unique one
  val kFun = ArithmeticFactory.make(getString("kFun", "avg"))
  // function to combine two k-shortest path from src to dst
  val combFun = ArithmeticFactory.make(getString("combFun", "avg"))


  override def toString(): String = {
    "%s,subGraph:%s,weighting:%s,wikiGraph:%s,threshold:%d,k:%d".formatLocal(java.util.Locale.US,
      super.toString(),
      weighting,
      threshold,
      k
    )
  }
}