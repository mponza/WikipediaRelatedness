package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.lucene

import java.nio.file.Paths

import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.core.KeywordAnalyzer
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.analysis.util.CharArraySet
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.{FSDirectory, IOContext, RAMDirectory}
import org.slf4j.LoggerFactory


class LuceneIndex {
  val logger = LoggerFactory.getLogger(classOf[LuceneIndex])

  val reader = loadIndexInMemory()
  val searcher = new IndexSearcher(reader)


  def loadIndexInMemory() : DirectoryReader = {
    logger.info("Loading Lucene index in memory...")

    val fsDir = FSDirectory.open(Paths.get(Configuration.wikipedia("lucene")))
    val directory = new RAMDirectory(fsDir, IOContext.DEFAULT)

    DirectoryReader.open(directory)
  }


  /**
    * @return List of Wikipedia IDs where wikiID is mentioned and the corresponding score.
    */
  def wikipediaConcepts(wikiID: Int, resultThreshold: Int = 625) : List[Tuple2[Int, Float]] = {
    val entWikiID = "ent_" + wikiID.toString

    val parser = new QueryParser("body", LuceneIndex.analyzer)
    val query = parser.createBooleanQuery("body", entWikiID)

    searcher.search(query, resultThreshold).scoreDocs.map { hit =>
      val wikiDocID = reader.document(hit.doc).getField("id").numericValue().intValue()

      (wikiDocID, hit.score)
    }.toList
  }
}


object LuceneIndex {
  lazy val analyzer = luceneEntityAnalyzer()

  def luceneEntityAnalyzer() : Analyzer = {
    val emptyStopWords = new CharArraySet(0, true)
    val bodyAnalyzer = new StandardAnalyzer(emptyStopWords)

    val analyzerMap = new java.util.HashMap[String, Analyzer]
    analyzerMap.put("body", bodyAnalyzer)

    new PerFieldAnalyzerWrapper(new KeywordAnalyzer(), analyzerMap)
  }
}