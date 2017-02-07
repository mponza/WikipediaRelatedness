package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.set

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessOptions}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.SetOperations
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory

class DiceRelatedness(val options: RelatednessOptions) extends Relatedness {

  protected val graph = WikiBVGraphFactory.make("sym")
  protected val operations = new SetOperations(graph)


  /**
    *      | Neigh(u) intersection Neigh(v) |
    * 2 * -----------------------------------
    *        | Neigh(u) | + | Neigh(v) |
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = {

    val num = operations.intersectionSize(srcWikiID, dstWikiID)
    val den = graph.successorArray(srcWikiID).size + graph.successorArray(dstWikiID).size

    2f * num / den
  }

  override def toString() = "Dice"

}