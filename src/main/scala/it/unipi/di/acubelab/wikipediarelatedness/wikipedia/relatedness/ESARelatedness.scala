package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelTask
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.lucene.LuceneIndex

class ESARelatedness(options: Map[String, Any])  extends  Relatedness {
  val lucene = new LuceneIndex
  val conceptThreshold = options.getOrElse("conceptThreshold", 625).toString.toDouble.toInt // wtf casting

  override def computeRelatedness(wikiRelTask: WikiRelTask) : Double = {
    relatedness("ent_%d".format(wikiRelTask.src.wikiID),
      "ent_%d".format(wikiRelTask.dst.wikiID))
  }

  def relatedness(srcWord: String, dstWord: String) : Double = {
    // src/dst scored concepts
    val srcSCs = lucene.wikipediaConcepts(srcWord)
    val dstSCs = lucene.wikipediaConcepts(dstWord)

    Similarity.cosineSimilarity(srcSCs, dstSCs)
  }
}
