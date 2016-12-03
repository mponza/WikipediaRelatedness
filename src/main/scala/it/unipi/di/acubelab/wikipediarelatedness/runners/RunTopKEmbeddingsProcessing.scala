package it.unipi.di.acubelab.wikipediarelatedness.runners

import java.io.File
import it.unipi.di.acubelab.wat.dataset.embeddings.EmbeddingsDataset
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.embeddings.ProcessTopKEmbeddings
import org.slf4j.LoggerFactory

/**
  * Class for pre-processing top-k embedding for each Wikipedia entity.
  * @param wikiRelTasks
  */
class RunTopKEmbeddingsProcessing(wikiRelTasks: List[WikiRelateTask]) extends Runner {
  val logger = LoggerFactory.getLogger(classOf[RunTopKEmbeddingsProcessing])
  val embProc = new ProcessTopKEmbeddings(wikiRelTasks)

  def run() = {

    models().foreach {
      case (name, path) =>

        logger.info("Running TopKEmbeddingProcessing of model %s".format(name))

        val w2v = EmbeddingsDataset.apply(new File(Configuration.wikipedia(name)))
        val cachePath = Configuration.topKEmbeddings(name)

        embProc.generate(cachePath, w2v)
    }

  }


  def models() = {
    Map(
      "sg" -> Configuration.wikipedia("sg"),
      "dwsg" -> Configuration.wikipedia("dwsg")
    )
  }

}
