package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.neural.line

import it.unimi.dsi.fastutil.floats.FloatArrayList
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.WikiEmbeddings
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j

class LINEEmbeddings(embeddings: Int2ObjectOpenHashMap[FloatArrayList]) extends WikiEmbeddings {

  override def apply(wikiID: Int): INDArray = Nd4j.create( embeddings.get(wikiID).toFloatArray() )

}
