package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness


import it.unipi.di.acubelab.wikipediarelatedness.latent.GraphSVD
import it.unipi.di.acubelab.wikipediarelatedness.options.GraphSVDOptions
import it.unipi.di.acubelab.wikipediarelatedness.utils.{Configuration, Similarity}
import org.slf4j.LoggerFactory

/**
  *
  * @param options
  *                {
  *                   "relatedness":    "graphSVD"
  *                   "eigen":   "right/left"
  *                   "length":
  *                }
  */
class GraphSVDRelatedness(options: GraphSVDOptions) extends Relatedness  {
  val logger = LoggerFactory.getLogger(classOf[GraphSVDRelatedness])

  val svd = new GraphSVD(Configuration.graphSVD(options.eigen), options.length)  // eigenNames.map(name => new GraphSVD(Configuration.graphSVD(name))).toList

  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {

    val srcVector = svd.embeddingVector(srcWikiID)
    val dstVector = svd.embeddingVector(dstWikiID)

    Similarity.cosineSimilarity(srcVector, dstVector)
  }

  override def toString(): String = {
    "GraphSVD_%s".format(options.eigen)
  }
}