package topk

import java.io.File

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unipi.di.acubelab.wat.dataset.embeddings.EmbeddingsDataset
import it.unipi.di.acubelab.wikipediarelatedness.utils.OldConfiguration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.TopKEmbeddingsCache
import org.nd4j.linalg.factory.Nd4j
import org.scalatest.{FlatSpec, Matchers}


class Embeddings extends FlatSpec with Matchers {
  val dw = EmbeddingsDataset(new File(OldConfiguration.wikipedia("dwsg")))

  val obama = 534366

  "w2v top-k pre-processed embeddings " should "be sorted by their cosine" in {
    TopKEmbeddingsCache.deepWalkSG.getTopK(obama).slice(0, 1000) should equal(topKSorted(obama).slice(0, 1000))
  }


  def topKSorted(wikiID: Int) = {
    val it = dw.topKSimilarFromWord("ent_%d".format(wikiID)).iterator()

    val topKEntities = new IntArrayList()
    while(it.hasNext) {
      val s = it.next()
      try {
        if (s.startsWith("ent_")) topKEntities.add(s.substring(4, s.length).toInt)
      } catch {
        case e: Exception => None
      }
    }

    topKEntities.toArray().sortBy( wid => dw.similarity("ent_%d".format(wikiID), "ent_%d".format(wid)) ).reverse
  }

  // Test to see if I understand well what I am trying to do with ND4J.
  "w2v top-k vector retrieved by word" should "be the same retrieved by vector" in {
    val obamaEmbedding = Nd4j.create(dw.embedding("ent_" + obama))
    val wegihtedEntities = dw.topKSimilarFromINDArray(obamaEmbedding)

    dw.topKSimilarFromWord("ent_" + obama) should equal(wegihtedEntities)
  }

}