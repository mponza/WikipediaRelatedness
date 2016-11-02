package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa.lemma.LemmaLuceneIndex
import org.apache.lucene.queryparser.classic.QueryParser

object ESA {
  val lucene = new LemmaLuceneIndex()
  val cache = new ESACache()

  /**
    * @param text LEMMATIZED text
    * @return List of Wikipedia IDs where wikiID is mentioned and the corresponding score.
    */
  def wikipediaConcepts(text: String, resultThreshold: Int): List[Tuple2[Int, Float]] = {
    val parser = new QueryParser("body", LuceneIndex.analyzer)
    val query = parser.createBooleanQuery("body", text)

    val threshold = if (resultThreshold >= 0) resultThreshold else Integer.MAX_VALUE

    val concepts = lucene.searcher.search(query, threshold).scoreDocs.map { hit =>
      val wikiDocID = lucene.reader.document(hit.doc).getField("id").stringValue().toInt
      (wikiDocID, hit.score)
    }.toList

    concepts.sortBy(_._1)
  }

  def wikipediaConcepts(text: String): List[Tuple2[Int, Float]] = wikipediaConcepts(text, 625)


  def wikipediaConcepts(wikiID: Int, resultThreshold: Int): List[Tuple2[Int, Float]] = {
    val cachedConcepts = cache.get(wikiID, resultThreshold)
    if (cachedConcepts != null) return cachedConcepts

    val wikiBody = lucene.wikipediaBody(wikiID)
    ESA.wikipediaConcepts(wikiBody, resultThreshold)
  }

  def wikipediaConcepts(wikiID: Int): List[Tuple2[Int, Float]] = wikipediaConcepts(wikiID, 625)
}