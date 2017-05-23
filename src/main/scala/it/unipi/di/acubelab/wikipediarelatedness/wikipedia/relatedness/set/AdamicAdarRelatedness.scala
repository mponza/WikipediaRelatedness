package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.set

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessOptions}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.SetOperations
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory

class AdamicAdarRelatedness(val options: RelatednessOptions) extends Relatedness {

  protected val graph = WikiBVGraphFactory.make("sym")
  protected val operations = new SetOperations(graph)

  override def computeRelatedness(task: WikiRelateTask) : Float = {
    Math.max(computeRelatedness(task.src.wikiID, task.dst.wikiID), 0f)

    //computeRelatedness(task.src.wikiID, task.dst.wikiID)
  }



  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = {
    val interWikiIDs = operations.intersection(srcWikiID, dstWikiID).toIntArray()

    interWikiIDs.filter(graph.outdegree(_) > 1).map {
      case wikiID =>
        1 / Math.log(graph.outdegree(wikiID))
    }.sum.toFloat
  }

  override def toString() = "AdamicAdar"

}