package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.neural

import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.neural.w2v.Word2Vec
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.embeddings.EmbeddingsRelatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessOptions
import org.slf4j.LoggerFactory


/**
  * Relatedness method which uses w2v-based embeddings.
  *
  * options.model can be (see reference.conf):
  *
  *             - corpus
  *  w2v.       - coocc
  *             - sg
  *
  *             - el_1st_dw
  *  el.        - el_1st
  *             - el_dw
  *
  *             - dw
  *  deepwalk.  - deep_corpus
  *             - dw.{10, 30, 50, 70, 90}
  *             - dwsg
  *
  */
class Word2VecRelatedness(options: RelatednessOptions) extends EmbeddingsRelatedness(options)  {
    val logger = LoggerFactory.getLogger(getClass)

    protected override val embeddings = new Word2Vec(
      Config.getString("wikipedia.neural." + options.model)
    )

    override def toString: String = "W2V_model:%s".format(options.model)
}
