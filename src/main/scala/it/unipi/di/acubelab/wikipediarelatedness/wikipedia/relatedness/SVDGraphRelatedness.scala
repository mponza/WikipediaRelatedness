package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelTask
import it.unipi.di.acubelab.wikipediarelatedness.latent.GraphSVD
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import org.slf4j.LoggerFactory

/**
  *
  * @param options
  *                {
  *                   "relatedness":    "graphSVD"
  *                   "eigenVectors":   "left/right/left,right"
  *                }
  */
class SVDGraphRelatedness(options: Map[String, Any]) extends Relatedness  {
  val logger = LoggerFactory.getLogger(classOf[EmbeddingRelatedness])
  val eigenNames = options.getOrElse("eigen", "right").toString().split(",")

  val svd = eigenNames.map(name => new GraphSVD(Configuration.graphSVD(name))).toList

  override def computeRelatedness(wikiRelTask: WikiRelTask): Double = {


    0.0
  }
}
