package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.clustering

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.clustering.LocalClustering
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessOptions}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.{Logger, LoggerFactory}


abstract class LocalClusteringRelatedness(val options: RelatednessOptions) extends Relatedness {
  def logger: Logger = LoggerFactory.getLogger(getClass)

  val graph = WikiBVGraphFactory.make(options.graph)
  val lc = new LocalClustering()


  /**
    * Keeps the top-threshold elements of the vector with the highest weight.
    * The returned vector is sorted by the first element.
    *
    * @param vector
    * @param threshold
    */
  protected def thresholdVector(vector: Seq[Tuple2[Int, Float]], threshold: Int) = {
    vector.sortBy(_._2).reverse.slice(0, threshold).sortBy(_._1)
  }

  override def toString(): String
}