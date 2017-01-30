package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.embeddings

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.Embeddings
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessOptions}
import org.slf4j.Logger


abstract class EmbeddingsRelatedness(val options: RelatednessOptions) extends Relatedness {
  protected val logger: Logger
  protected val embeddings: Embeddings


  protected def toString: String
}