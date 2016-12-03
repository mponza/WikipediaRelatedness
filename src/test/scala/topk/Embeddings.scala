package topk

import java.io.File

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unipi.di.acubelab.wat.dataset.embeddings.EmbeddingsDataset
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.embeddings.TopKEmbeddings
import org.scalatest.{FlatSpec, Matchers}


class Embeddings extends FlatSpec with Matchers {
  val dw = EmbeddingsDataset(new File(Configuration.wikipedia("dwsg")))

  "w2v top-k pre-processed embeddings " should "be sorted by their cosine" in {
    val silvio = 26909
    TopKEmbeddings.deepWalkSG.getTopKWikiIDs(silvio) should equal(topKSorted(silvio))
  }


  def topKSorted(wikiID: Int) = {
    val it = dw.topKSimilar("ent_%d".format(wikiID)).iterator()

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

}