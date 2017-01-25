package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.neural

import java.io.File

import it.unipi.di.acubelab.wat.dataset.embeddings.EmbeddingsDataset
import it.unipi.di.acubelab.wikipediarelatedness.options.Word2VecOptions
import it.unipi.di.acubelab.wikipediarelatedness.utils.OldConfiguration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory

/**
  *
  * @param options
  *                {
  *                   "model": corpus/deepWalk/deepCorpus/coOccurrence
  *                }
  */
class Word2VecRelatedness(options: Word2VecOptions = new Word2VecOptions()) extends Relatedness  {
    val logger = LoggerFactory.getLogger(classOf[Word2VecRelatedness])
    val w2v = loadw2v(options.model)

    def loadw2v(modelName : String) : EmbeddingsDataset  = {
      logger.info("Loading w2v %s model...".format(modelName))
      val w2vPath = OldConfiguration.wikipedia(modelName)

      EmbeddingsDataset.apply(new File(w2vPath))
    }

    def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
      val srcEntWikiID = "ent_" + srcWikiID
      val dstEntWikiID = "ent_" + dstWikiID

      val cosine = w2v.similarity(srcEntWikiID, dstEntWikiID)
      //if (cosine < 0) logger.warn("Negative cosine between %s.".format(wikiRelTask.wikiTitleString()))

      cosine
    }

    override def toString(): String = {
      "W2V-%s".format(options.model)
    }
}
