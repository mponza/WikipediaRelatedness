package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa.lemma

import it.unipi.di.acubelab.wikipediarelatedness.utils.CoreNLP
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa.LuceneIndex
import org.slf4j.LoggerFactory


class LemmaLuceneIndex() extends LuceneIndex() {

  override def getLogger() = LoggerFactory.getLogger(classOf[LemmaLuceneIndex])

  override def wikipediaBody(wikiID: Int) : String = {
    val rawBody = super.wikipediaBody(wikiID)
    val lemmaBody = CoreNLP.lemmatize(rawBody) mkString(" ")

    lemmaBody
  }
}