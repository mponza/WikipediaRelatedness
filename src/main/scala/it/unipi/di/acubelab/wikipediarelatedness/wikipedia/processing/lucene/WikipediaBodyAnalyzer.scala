package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.lucene

import java.io.Reader

import org.apache.lucene.analysis.Analyzer.TokenStreamComponents
import org.apache.lucene.analysis.core.{LowerCaseFilter, StopAnalyzer, StopFilter, WhitespaceTokenizer}
import org.apache.lucene.analysis.Analyzer
import org.slf4j.LoggerFactory


/**
  * Analyzer which takes into account a body where Wikipedia entities are
  * marked as ent_wikiID.
  */
class WikipediaBodyAnalyzer extends Analyzer {
  val logger = LoggerFactory.getLogger(classOf[WikipediaBodyAnalyzer])

  override def createComponents(fieldName : String) : TokenStreamComponents = {
    if(fieldName != "body") {
      logger.warn("You are using WikipediaBodyAnalyzer for a fieldName which is not 'body': %s"
        .format(fieldName))
    }

    val whiteTokenizer = new WhitespaceTokenizer()
    val lowerFilter = new LowerCaseFilter(whiteTokenizer)
    val stopFilter = new StopFilter(lowerFilter,  StopAnalyzer.ENGLISH_STOP_WORDS_SET)

    new TokenStreamComponents(whiteTokenizer, stopFilter) {
      override def setReader(reader: Reader): Unit = {
        super.setReader(reader)
      }
    }
  }
}

