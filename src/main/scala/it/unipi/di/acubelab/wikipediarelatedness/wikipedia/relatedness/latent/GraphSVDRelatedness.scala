package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.latent

import it.unipi.di.acubelab.wikipediarelatedness.utils.{Config, Similarity}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessOptions}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.latent.GraphSVD
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory


class GraphSVDRelatedness(val options: RelatednessOptions) extends Relatedness  {
  val logger = LoggerFactory.getLogger(classOf[GraphSVDRelatedness])

  val svd = new GraphSVD(options.model, options.threshold)  // eigenNames.map(name => new GraphSVD(Configuration.graphSVD(name))).toList

  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {

    val srcVector = svd.embeddingVector(srcWikiID)
    val dstVector = svd.embeddingVector(dstWikiID)

    Similarity.cosineSimilarity(srcVector, dstVector)
  }

  override def toString(): String = {
    "GraphSVD_%s".format(options.model)
  }
}