package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.neural

import java.util.Locale

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessOptions}
import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.neural.line.LINE
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.embeddings.EmbeddingsRelatedness
import org.slf4j.LoggerFactory


class LINERelatedness(options: RelatednessOptions) extends EmbeddingsRelatedness(options) {
  val logger = LoggerFactory.getLogger(classOf[LINERelatedness])

  val embeddings = new LINE(options.size, options.order, options.negative, options.sample, options.rho)


  override def toString: String = {
    "LINERelatedness_size:%d,order:%d,neg:%d,sample:%d,rho:%1.3f"
      .formatLocal(Locale.US, options.size, options.order, options.negative, options.sample, options.rho)
  }
}