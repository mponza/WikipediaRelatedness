package it.unipi.di.acubelab.graphrel.wikipedia.relatedness

import java.io.File

import it.cnr.isti.hpc.{LinearAlgebra, Word2VecCompress}
import it.unimi.dsi.fastutil.io.BinIO
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

      if (cosine < 0) logger.warn("Negative cosine between %s and %s".format(wikiRelTask.src, wikiRelTask.dst))
      // Cosine scaling: from [-1, 1] to [0, 1]
      math.min(math.max(cosine, 0.0), 1.0)
      //(cosine + 1) / 2
    }

    /*def cosineSimilarity(srcWikiID: Int, dstWikiID: Int) : Double = {
      val cosine = w2v.similarity("ent_" + srcWikiID, "ent_" + dstWikiID)
      val srcVec = w2v.get("ent_" + srcWikiID)
      val dstVec = w2v.get("ent_" + dstWikiID)

      if(srcVec == null || dstVec == null) return 0.0

      val distance = LinearAlgebra.inner(srcVec.length, srcVec, 0, dstVec, 0)
      val srcNorm = math.sqrt(LinearAlgebra.inner(srcVec.length, srcVec, 0, srcVec, 0))
      val dstNorm = math.sqrt(LinearAlgebra.inner(srcVec.length, dstVec, 0, dstVec, 0))

      val cosine = distance / (srcNorm * dstNorm)

      cosine.toDouble
    }*/

    override def toString(): String = {
      "W2V-%s".format(modelName)
    }
}
