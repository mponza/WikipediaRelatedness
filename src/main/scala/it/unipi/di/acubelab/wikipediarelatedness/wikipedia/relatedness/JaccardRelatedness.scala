package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.options.JaccardOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.WikiGraph

/**
  *
  * @param options
  *                {
  *                   graph: inGraph/outGraph/symGraph
  *                }
  */
class JaccardRelatedness(val options: JaccardOptions) extends Relatedness {
  val graph = WikiGraph.wikiBVGraph(options.graph)

  def computeRelatedness(wikiRelTask: WikiRelateTask) : Double = {
    val srcWikiID = wikiRelTask.src.wikiID
    val dstWikiID = wikiRelTask.dst.wikiID

    val intersection = graph.linkIntersection(srcWikiID, dstWikiID)
    if (intersection == 0) return 0.0

    val sizeA = graph.outdegree(srcWikiID)
    val sizeB = graph.outdegree(dstWikiID)

    intersection / (sizeA + sizeB - intersection).toDouble
  }

  override def toString () : String = { "Jaccard_%s".format(options.graph) }
}
