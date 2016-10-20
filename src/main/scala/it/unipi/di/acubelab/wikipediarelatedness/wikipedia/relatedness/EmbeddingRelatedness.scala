package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import java.io.File

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import it.unipi.di.acubelab.wat.dataset.embeddings.EmbeddingsDataset
import it.unipi.di.acubelab.wikipediarelatedness.options.EmbeddingOptions
import org.slf4j.LoggerFactory

/**
  *
  * @param options
  *                {
  *                   "model": corpus/deepWalk/deepCorpus/coOccurrence
  *                }
  */
class EmbeddingRelatedness(options: EmbeddingOptions) extends Relatedness  {
    val logger = LoggerFactory.getLogger(classOf[EmbeddingRelatedness])
    val w2v = loadw2v(options.model)

    def loadw2v(modelName : String) : EmbeddingsDataset  = {
      logger.info("Loading w2v %s model...".format(modelName))
      val w2vPath = Configuration.wikipedia(modelName)

      EmbeddingsDataset.apply(new File(w2vPath))
    }

    def computeRelatedness(wikiRelTask: WikiRelateTask) : Double = {
      val srcEntWikiID = "ent_" + wikiRelTask.src.wikiID
      val dstEntWikiID = "ent_" + wikiRelTask.dst.wikiID

      val cosine = w2v.similarity(srcEntWikiID, dstEntWikiID).toDouble

      if (cosine < 0) logger.warn("Negative cosine between %s.".format(wikiRelTask.wikiTitleString()))

      cosine
    }

    override def toString(): String = {
      "W2V-%s".format(options.model)
    }
}
