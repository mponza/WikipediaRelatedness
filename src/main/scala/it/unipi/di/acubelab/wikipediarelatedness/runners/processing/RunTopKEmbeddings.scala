package it.unipi.di.acubelab.wikipediarelatedness.runners.processing

import java.io.File

import it.unipi.di.acubelab.wat.dataset.embeddings.EmbeddingsDataset
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.runners.Runner
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.embeddings.ProcessTopKEmbeddings
import org.slf4j.LoggerFactory

/**
  * Class for pre-processing top-k embeddings for each Wikipedia entity and entity pairs.
  *
  * @param wikiRelTasks
  */
class RunTopKEmbeddings(wikiRelTasks: List[WikiRelateTask]) extends Runner {
  val logger = LoggerFactory.getLogger(classOf[RunTopKEmbeddings])
  val embProc = new ProcessTopKEmbeddings(wikiRelTasks)


  def run() = {

    models().foreach {
      case (name, path) =>

        logger.info("Running TopKEmbeddingProcessing of model %s".format(name))

        val w2v = EmbeddingsDataset.apply(new File(Configuration.wikipedia(name)))
        val cacheDir = Configuration.topKEmbeddings(name)

        embProc.generateTopK(cacheDir, w2v)
    }

  }


  def models() = {
    Map(
      "sg" -> Configuration.wikipedia("sg"),
      "dwsg" -> Configuration.wikipedia("dwsg")
    )
  }

}
