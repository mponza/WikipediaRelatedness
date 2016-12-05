package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa.lemma.LemmaLuceneIndex
import org.apache.lucene.queryparser.classic.QueryParser

/**
  * Static class which implements ESA upon the Wikipedia Corpus.
  * Concept vectors are always sorted by their SCORE (not wikiID!).
  */
object ESA {
  lazy val lucene = new LemmaLuceneIndex()
  val cache = new ESACache()

  /**
    * Returns ESA's concepts vector for a given text. This method DOES NOT use the cache.
    * @param text LEMMATIZED text
    * @return List of Wikipedia IDs where wikiID is mentioned and the corresponding score.
    *         The best resultThreshold concepts are returned by decreasing order by their score.
    */
  def wikipediaConcepts(text: String, resultThreshold: Int): List[Tuple2[Int, Float]] = {
    val parser = new QueryParser("body", LuceneIndex.analyzer)
    val query = parser.createBooleanQuery("body", text)

    val threshold = if (resultThreshold >= 0) resultThreshold else Integer.MAX_VALUE

    val concepts = lucene.searcher.search(query, threshold).scoreDocs.map { hit =>
      val wikiDocID = lucene.reader.document(hit.doc).getField("id").stringValue().toInt
      (wikiDocID, hit.score)
    }.toList.filter(_._1 > 0)

    concepts
  }

  def wikipediaConcepts(text: String): List[Tuple2[Int, Float]] = wikipediaConcepts(text, 650)


  /**
    * Returns ESA's concepts vector of wikiID. If available, it exploit the cache.
    * @param wikiID
    * @param resultThreshold
    * @return
    */
  def wikipediaConcepts(wikiID: Int, resultThreshold: Int): List[Tuple2[Int, Float]] = {
    val cachedConcepts = cache.get(wikiID, resultThreshold)

    if (cachedConcepts != null) return cachedConcepts

    val wikiBody = lucene.wikipediaBody(wikiID)
    ESA.wikipediaConcepts(wikiBody, resultThreshold)

  }

  def wikipediaConcepts(wikiID: Int): List[Tuple2[Int, Float]] = wikipediaConcepts(wikiID, 650)


  // Not yet used... to be cached.
  def wikipediaConcepts(srcWikiID: Int, dstWikiID: Int, resultThreshold: Int = 650) : List[Tuple2[Int, Float]] = {
    val srcBody = lucene.wikipediaBody(srcWikiID)
    val dstBody = lucene.wikipediaBody(dstWikiID)

    wikipediaConcepts(srcBody + " " + dstBody, resultThreshold)
  }
}