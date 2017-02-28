package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.neural.w2v

import java.io.File

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import it.unipi.di.acubelab.wat.dataset.embeddings.EmbeddingsDataset
import it.unipi.di.acubelab.wikipediarelatedness.utils.StopWords
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.WikiEmbeddings
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.esa.WikipediaBodyAnalyzer
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.ops.transforms.Transforms
import org.slf4j.LoggerFactory


/**
  * Wrapper of EmbeddingDataset.
  *
  * @param modelPath
  */
class Word2VecEmbeddings(modelPath: String) extends WikiEmbeddings {
  protected val logger = LoggerFactory.getLogger(getClass)

  protected val w2vDataset = {
    if (modelPath == "") throw new IllegalArgumentException("Model not specificed!.")
    EmbeddingsDataset.apply(new File(modelPath))
  }

  override def apply(wikiID: Int): INDArray = {
    if (!w2vDataset.contains("ent_" + wikiID)) {

      //logger.warn("%d not present in %s w2v model. Returning null vector."format(wikiID, WikiTitleID.map(wikiID)))
      null

    } else {

      Nd4j.create(w2vDataset.embedding("ent_" + wikiID))
    }
  }


  //
  // WARNING: Beta
  def textVector(words: Seq[String]) = {
    val textNoStopWords = words.filter( !StopWords.isStopWord(_) )
    w2vDataset.contextVector(textNoStopWords)
  }

  /**
    * Cosine similarity between two texts which are subsequently mapped into an average w2v vector.
    *
    * @param srcText
    * @param dstText
    * @return
    */
  def textCosine(srcText: Seq[String], dstText : Seq[String]) =
    Transforms.cosineSim( textVector(srcText), textVector(dstText) ).toFloat

}
