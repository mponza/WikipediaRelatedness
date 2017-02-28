package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.embeddings.neural

import it.unipi.di.acubelab.wikipediarelatedness.utils.{Config, WikipediaCorpus}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.neural.w2v.{Word2Vec, Word2VecEmbeddings}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.neural.Word2VecRelatedness
import org.slf4j.LoggerFactory


/**
  * Doc2Vec where the vector of an entity is the average of its words.
  *
  * @param options
  */
class Word2VecDoc2VecRelatedness(options: RelatednessOptions) extends  Word2VecRelatedness(options)  {
  override val logger = LoggerFactory.getLogger(getClass)

  protected override val embeddings = null

  protected val model = new Word2VecEmbeddings(
    Config.getString("wikipedia.neural." + options.model)
  )

  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    logger.debug("%d %d".format(srcWikiID, dstWikiID))
    val srcText = WikipediaCorpus.getWords(srcWikiID)//.filter(_.startsWith("ent_"))
    val dstText = WikipediaCorpus.getWords(dstWikiID)//.filter(_.startsWith("ent_"))

    model.textCosine(srcText, dstText)
  }

  override def toString: String = "W2VD2V_model:%s".format(options.model)
}
