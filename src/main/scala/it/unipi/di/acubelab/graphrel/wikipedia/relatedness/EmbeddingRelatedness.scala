package it.unipi.di.acubelab.graphrel.wikipedia.relatedness

import java.io.File

import it.unipi.di.acubelab.graphrel.dataset.WikiRelTask
import it.unipi.di.acubelab.graphrel.utils.Configuration
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors

class EmbeddingRelatedness(options: Map[String, Any]) extends Relatedness  {
    val modelName = if (options.contains("w2v")) options("model").toString else "corpus"
    val w2v = loadw2v(modelName : String)


    def loadw2v(modelName : String) : WordVectors = {
      val w2vPath = Configuration.wikipedia(modelName)
      WordVectorSerializer.loadGoogleModel(new File(w2vPath), true)
    }

    def computeRelatedness(wikiRelTask: WikiRelTask) : Double = {
      // To be checked!
      val srcVec = w2v.getWordVector(wikiRelTask.src.wikiID.toString)
      val dstVec = w2v.getWordVector(wikiRelTask.dst.wikiID.toString)

      (srcVec, dstVec).zipped.map(_ * _).sum
    }
}
