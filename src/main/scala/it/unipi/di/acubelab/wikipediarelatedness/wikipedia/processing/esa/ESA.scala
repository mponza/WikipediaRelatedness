package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa

import it.unipi.di.acubelab.wikipediarelatedness.utils.CoreNLP
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa.lemma.LemmaLuceneIndex
import org.apache.lucene.queryparser.classic.QueryParser

object ESA {
  val lucene = new LemmaLuceneIndex()

  /**
    * @return List of Wikipedia IDs where wikiID is mentioned and the corresponding score.
    */
  def wikipediaConcepts(text: String, resultThreshold: Int = 625): List[Tuple2[Int, Float]] = {
    val parser = new QueryParser("body", LuceneIndex.analyzer)
    val coreText = CoreNLP.lemmatize(text).mkString(" ")

    val query = parser.createBooleanQuery("body", coreText)

    val threshold = if (resultThreshold >= 0) resultThreshold else Integer.MAX_VALUE

    val x = lucene.searcher.search(query, threshold).scoreDocs.map { hit =>
      val wikiDocID = lucene.reader.document(hit.doc).getField("id").stringValue().toInt
      (wikiDocID, hit.score)
    }.toList

    x.sortBy(_._1)
  }


  def wikipediaConcepts(wikiID: Int, resultThreshold: Int = 625): List[Tuple2[Int, Float]] = {
    val wikiBody = wikipediaBody(wikiID)
    wikipediaConcepts(wikiBody, resultThreshold)
  }


  def wikipediaBody(wikiID: Int) : String = {
    val parser = new QueryParser("id", LuceneIndex.analyzer)
    val query = parser.createBooleanQuery("id", wikiID.toString)

    val wikiDoc = lucene.searcher.search(query, 1).scoreDocs(0).doc
    lucene.reader.document(wikiDoc).getField("body").toString
  }


  def vectorSpaceProjection(wikiID: Int) : String = {
    //val text = wikipediaBody(wikiID)
    val parser = new QueryParser("id", LuceneIndex.analyzer)
    val query = parser.createBooleanQuery("id", wikiID.toString)

    val wikiDoc = lucene.searcher.search(query, 1).scoreDocs(0).doc

    val terms = lucene.reader.getTermVector(wikiDoc, "body")
    val termsEnum = terms.iterator()
    var bytesRef = termsEnum.next()
    while(bytesRef  != null){
      System.out.println("BytesRef: " + bytesRef.utf8ToString())
      System.out.println("docFreq: " + termsEnum.docFreq())
      System.out.println("totalTermFreq: " + termsEnum.totalTermFreq())
      bytesRef = termsEnum.next()
    }

    ""
  }
}
