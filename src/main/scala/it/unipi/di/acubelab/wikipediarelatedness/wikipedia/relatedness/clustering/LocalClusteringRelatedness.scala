package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.clustering

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.clustering.LocalClustering
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessOptions}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.{Logger, LoggerFactory}


abstract class LocalClusteringRelatedness(val options: RelatednessOptions) extends Relatedness {
  def logger: Logger = LoggerFactory.getLogger(getClass)

  val graph = WikiBVGraphFactory.makeWikiBVGraph(options.graph)
  val lc = new LocalClustering()

  override def toString(): String
}