package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.embeddings.neural

import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.neural.w2v.Word2Vec
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.neural.Word2VecRelatedness
import org.slf4j.LoggerFactory


class DocW2VRelatedness(options: RelatednessOptions) extends  Word2VecRelatedness(options)  {
  override val logger = LoggerFactory.getLogger(getClass)

  protected override val embeddings = new Word2Vec(
    Config.getString("wikipedia.neural." + options.model)
  )

  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    embeddings.cosine(srcWikiID, dstWikiID)
  }

  override def toString: String = "W2V_model:%s".format(options.model)
}
