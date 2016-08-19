package it.unipi.di.acubelab.graphrel.wikipedia.relatedness

import java.io.File

import it.unipi.di.acubelab.graphrel.dataset.WikiRelTask
import it.unipi.di.acubelab.graphrel.utils.Configuration
import it.unipi.di.acubelab.wat.dataset.embeddings.EmbeddingsDataset
import org.slf4j.LoggerFactory

/**
  *
  * @param options
  *                {
  *                   "model": corpus/deepWalk/deepCorpus/coOccurrence
  *                }
  */
class EmbeddingRelatedness(options: Map[String, Any]) extends Relatedness  {
    val logger = LoggerFactory.getLogger(classOf[EmbeddingRelatedness])
    val modelName = options.getOrElse("model", "corpus").toString
    val w2v = loadw2v(modelName)

    def loadw2v(modelName : String) : EmbeddingsDataset  = {
      logger.info("Loading w2v %s model...".format(modelName))
      val w2vPath = Configuration.wikipedia(modelName)

      EmbeddingsDataset.apply(new File(w2vPath))
    }

    def computeRelatedness(wikiRelTask: WikiRelTask) : Double = {
      val srcEntWikiID = "ent_" + wikiRelTask.src.wikiID
      val dstEntWikiID = "ent_" + wikiRelTask.dst.wikiID

      val cosine = w2v.similarity(srcEntWikiID, dstEntWikiID).toDouble

      if (cosine < 0) logger.warn("Negative cosine between %s and %s".format(wikiRelTask.wikiTitleString()))

      math.min(math.max(cosine, 0.0), 1.0)
    }

    override def toString(): String = {
      "W2V-%s".format(modelName)
    }
}
