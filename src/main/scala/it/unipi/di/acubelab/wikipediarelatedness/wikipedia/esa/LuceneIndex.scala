package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.esa

import java.nio.file.Paths

import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.core.KeywordAnalyzer
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper
import org.apache.lucene.index.{DirectoryReader, Term}
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{BooleanQuery, IndexSearcher}
import org.apache.lucene.search.similarities.{BM25Similarity, ClassicSimilarity}
import org.apache.lucene.store.MMapDirectory
import org.slf4j.LoggerFactory

import scala.collection.mutable



/**
  * Manager of an index built with Lucene.
  *
  */
class LuceneIndex {
  protected val logger = LoggerFactory.getLogger(getClass)

  val reader = loadIndexInMemory()
  val searcher = new IndexSearcher(reader)

  searcher.setSimilarity(new BM25Similarity())
  BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE)


  /**
    * Loads the index from disk.
    *
    * @return
    */
  protected def loadIndexInMemory(): DirectoryReader = {
    logger.info("Loading Lucene index in memory...")

    val fsDir = Paths.get(Config.getString("wikipedia.lucene"))
    val directory = new MMapDirectory(fsDir)

    DirectoryReader.open(directory)
  }


  /**
    * Retrieves the Wikipedia document from its wikiID.
    *
    * @param wikiID
    * @return
    */
  def wikipediaBody(wikiID: Int) : String = {
    val parser = new QueryParser("id", LuceneIndex.analyzer)
    val query = parser.createBooleanQuery("id", wikiID.toString)

    val wikiDoc = searcher.search(query, 1).scoreDocs(0).doc
    reader.document(wikiDoc).getField("body").toString
  }


  /**
    * Vector Space Model, sorted by term.
    *
    * @param wikiID
    * @return
    */
  def vectorSpaceProjection(wikiID: Int) : Seq[(String, Float)] = {
    val vector = mutable.HashMap.empty[String, Float]

    //val text = wikipediaBody(wikiID)
    val parser = new QueryParser("id", LuceneIndex.analyzer)
    val query = parser.createBooleanQuery("id", wikiID.toString)

    val wikiDoc = searcher.search(query, 1).scoreDocs(0).doc

    val sim = new ClassicSimilarity
    val docCount = reader.numDocs()

    val terms = reader.getTermVector(wikiDoc, "body")
    val termsEnum = terms.iterator()
    var bytesRef = termsEnum.next()

    while(bytesRef  != null){

      val str = bytesRef.utf8ToString()

      val tf = termsEnum.totalTermFreq()
      val df = reader.docFreq( new Term("body", bytesRef.utf8ToString() ) )
      val tfidf = sim.idf(df, docCount)

      vector(str) = tfidf

      bytesRef = termsEnum.next()
    }

    vector.toSeq
  }
}


object LuceneIndex {
  lazy val analyzer = luceneEntityAnalyzer()

  def luceneEntityAnalyzer() : Analyzer = {
    val bodyAnalyzer = new WikipediaBodyAnalyzer()

    val analyzerMap = new java.util.HashMap[String, Analyzer]
    analyzerMap.put("body", bodyAnalyzer)

    new PerFieldAnalyzerWrapper(new KeywordAnalyzer(),  analyzerMap )
  }
}