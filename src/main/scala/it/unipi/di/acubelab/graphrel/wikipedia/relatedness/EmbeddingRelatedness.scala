package it.unipi.di.acubelab.graphrel.wikipedia.relatedness

import java.io.File

import it.cnr.isti.hpc.{LinearAlgebra, Word2VecCompress}
import it.unimi.dsi.fastutil.io.BinIO
import it.unipi.di.acubelab.graphrel.dataset.WikiRelTask
import it.unipi.di.acubelab.graphrel.utils.Configuration
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
    val w2v = loadw2v(modelName : String)

    def loadw2v(modelName : String) : Word2VecCompress  = {
      logger.info("Loading w2v %s model...".format(modelName))
      val w2vPath = Configuration.wikipedia(modelName)
      BinIO.loadObject(w2vPath).asInstanceOf[Word2VecCompress]
    }

    def computeRelatedness(wikiRelTask: WikiRelTask) : Double = {
      val cosine = cosineSimilarity(wikiRelTask.src.wikiID, wikiRelTask.dst.wikiID)

      // Cosine scaling: from [-1, 1] to [0, 1]
      (cosine + 1) / 2
    }

    def cosineSimilarity(srcWikiID: Int, dstWikiID: Int) : Double = {
      val srcVec = w2v.get("ent_" + srcWikiID)
      val dstVec = w2v.get("ent_" + dstWikiID)

      val distance = LinearAlgebra.inner(srcVec.length, srcVec, 0, dstVec, 0)
      val srcNorm = math.sqrt(LinearAlgebra.inner(srcVec.length, srcVec, 0, srcVec, 0))
      val dstNorm = math.sqrt(LinearAlgebra.inner(srcVec.length, dstVec, 0, dstVec, 0))

      val cosine = distance / (srcNorm * dstNorm)

      cosine.toDouble
    }

    override def toString(): String = {
      "W2V-%s".format(modelName)
    }
}
