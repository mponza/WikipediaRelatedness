package it.unipi.di.acubelab.wat

import java.io.File

import edu.berkeley.nlp.lm.NgramLanguageModel
import edu.berkeley.nlp.lm.io.LmReaders
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import org.slf4j.LoggerFactory

object LanguageModel {
  protected val models = collection.mutable.Map.empty[File, NgramLanguageModel[String]]
  protected val logger = LoggerFactory.getLogger(getClass)
  lazy val model = LmReaders.readLmBinary[String](Config.getString("wikipedia.lm"))

  def loadModel(): NgramLanguageModel[String] = {
    val modelFile = new File(Config.getString("wikipedia.lm"))

    synchronized {
      models.getOrElseUpdate(modelFile, {
        LmReaders.readLmBinary(modelFile.getAbsolutePath)
      })
    }
  }
}