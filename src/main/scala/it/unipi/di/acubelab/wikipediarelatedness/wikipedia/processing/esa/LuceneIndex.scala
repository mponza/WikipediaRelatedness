package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa

import java.nio.file.Paths

import it.unipi.di.acubelab.wikipediarelatedness.utils.{Configuration, CoreNLP}
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.core.KeywordAnalyzer
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{BooleanQuery, IndexSearcher}
import org.apache.lucene.search.similarities.BM25Similarity
import org.apache.lucene.store.{FSDirectory, IOContext, MMapDirectory, RAMDirectory}
import org.slf4j.LoggerFactory


class LuceneIndex {
  val logger = getLogger()

  val reader = loadIndexInMemory()
  val searcher = new IndexSearcher(reader)
  searcher.setSimilarity(new BM25Similarity())

  BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE)


  def getLogger() = LoggerFactory.getLogger(classOf[LuceneIndex])


  def loadIndexInMemory(): DirectoryReader = {
    logger.info("Loading Lucene index in memory...")

    val fsDir = Paths.get(Configuration.wikipedia("lucene"))
    val directory = new MMapDirectory(fsDir)

    DirectoryReader.open(directory)
  }

  def wikipediaBody(wikiID: Int) : String = {
    val parser = new QueryParser("id", LuceneIndex.analyzer)
    val query = parser.createBooleanQuery("id", wikiID.toString)

    val wikiDoc = searcher.search(query, 1).scoreDocs(0).doc
    reader.document(wikiDoc).getField("body").toString
  }


  def vectorSpaceProjection(wikiID: Int) : String = {
    //val text = wikipediaBody(wikiID)
    val parser = new QueryParser("id", LuceneIndex.analyzer)
    val query = parser.createBooleanQuery("id", wikiID.toString)

    val wikiDoc = searcher.search(query, 1).scoreDocs(0).doc

    val terms = reader.getTermVector(wikiDoc, "body")
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

object LuceneIndex {
  lazy val analyzer = luceneEntityAnalyzer()

  def luceneEntityAnalyzer() : Analyzer = {
    val bodyAnalyzer = new WikipediaBodyAnalyzer()

    val analyzerMap = new java.util.HashMap[String, Analyzer]
    analyzerMap.put("body", bodyAnalyzer)

    new PerFieldAnalyzerWrapper(new KeywordAnalyzer(),  analyzerMap )
  }
}