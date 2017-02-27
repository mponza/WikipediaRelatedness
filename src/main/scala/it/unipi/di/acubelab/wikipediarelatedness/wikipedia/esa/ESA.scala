package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.esa

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.esa.lemma.LemmaLuceneIndex
import org.slf4j.LoggerFactory
//import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.esa.ESATopKCache
import org.apache.lucene.queryparser.classic.QueryParser


/**
  * Static class which implements ESA upon the Wikipedia Corpus.
  * Concept vectors are always sorted by their SCORE (not wikiID!).
  *
  */

// Efficiency improvements:
// Experiment MoreLikeThis: http://stackoverflow.com/questions/7657673/how-to-find-similar-documents/7657757#7657757
// See this implementation: https://github.com/pvoosten/explicit-semantic-analysis (what about TextQuery?)
// ****************************************
object ESA {
  protected val logger = LoggerFactory.getLogger("ESA")
  lazy val lucene = new LemmaLuceneIndex()
  //val cache = new ESATopKCache()


  /**
    * Returns ESA's concepts vector for a given text. This method DOES NOT use the cache.
    *
    * @param text LEMMATIZED text
    * @return List of Wikipedia IDs where wikiID is mentioned and the corresponding score.
    *         The best resultThreshold concepts are returned by decreasing order by their score.
    */
  def wikipediaConcepts(text: String, resultThreshold: Int): Seq[Tuple2[Int, Float]] = {
    val parser = new QueryParser("body", LuceneIndex.analyzer)
    val query = parser.createBooleanQuery("body", text)

    // Alternatives
    //val query = parser.parse(text)
    //val query = parser.createPhraseQuery("body", text)

    val threshold = if (resultThreshold >= 0) resultThreshold else Integer.MAX_VALUE

    val concepts = lucene.searcher.search(query, threshold).scoreDocs.map { hit =>
      val wikiDocID = lucene.reader.document(hit.doc).getField("id").stringValue().toInt
      (wikiDocID, hit.score)
    }.toList.filter(_._1 > 0)

    concepts
  }

  def wikipediaConcepts(text: String): Seq[Tuple2[Int, Float]] = wikipediaConcepts(text, 2000)


  /**
    * Returns ESA's concepts vector of wikiID.
    *
    * @param wikiID
    * @param resultThreshold
    * @return
    */
  def wikipediaConcepts(wikiID: Int, resultThreshold: Int): Seq[Tuple2[Int, Float]] = {
    //val cachedConcepts = cache.topKScoredEntities(wikiID, resultThreshold)

    //if (cachedConcepts != null) return cachedConcepts

    val wikiBody = lucene.wikipediaBody(wikiID)
    ESA.wikipediaConcepts(wikiBody, resultThreshold)

  }

  def wikipediaConcepts(wikiID: Int): Seq[Tuple2[Int, Float]] = wikipediaConcepts(wikiID, 2000)


  // Not yet used... to be cached.

  def wikipediaConcepts(srcWikiID: Int, dstWikiID: Int, resultThreshold: Int = 2000) : Seq[Tuple2[Int, Float]] = {
    val srcBody = lucene.wikipediaBody(srcWikiID)
    val dstBody = lucene.wikipediaBody(dstWikiID)

    wikipediaConcepts(srcBody + " " + dstBody, resultThreshold)
  }
}