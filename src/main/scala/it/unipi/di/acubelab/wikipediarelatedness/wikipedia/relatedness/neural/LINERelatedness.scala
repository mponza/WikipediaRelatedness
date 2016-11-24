package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.neural

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import it.unipi.di.acubelab.wikipediarelatedness.options.LINEOptions
import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.neural.LINE
import org.slf4j.LoggerFactory


class LINERelatedness(val options: LINEOptions = new LINEOptions()) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[Word2VecRelatedness])

  val line = new LINE(options.size, options.order, options.negative)

  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    val srcEmbeddings = line.embeddings.get(srcWikiID)
    val dstEmbeddings = line.embeddings.get(dstWikiID)

    Similarity.cosineSimilarity(srcEmbeddings, dstEmbeddings)
  }

  override def toString(): String = {
    "LINE-%s".format(options)
  }
}
