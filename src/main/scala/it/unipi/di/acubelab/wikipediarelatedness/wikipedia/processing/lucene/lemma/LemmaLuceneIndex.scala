package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.lucene.lemma

import it.unipi.di.acubelab.wikipediarelatedness.utils.CoreNLP
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.lucene.{LuceneIndex, WikipediaBodyAnalyzer}
import org.slf4j.LoggerFactory


class LemmaLuceneIndex extends LuceneIndex {
  override val logger = LoggerFactory.getLogger(classOf[LemmaLuceneIndex])

  override def wikipediaConcepts(text: String, resultThreshold: Int = 625) : List[Tuple2[Int, Float]] = {
    val lemmaWords = CoreNLP.lemmatize(text) mkString(" ")
    super.wikipediaConcepts(lemmaWords, resultThreshold)
  }
}