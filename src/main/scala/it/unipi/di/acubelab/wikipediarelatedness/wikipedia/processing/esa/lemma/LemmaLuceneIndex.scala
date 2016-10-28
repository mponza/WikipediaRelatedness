package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa.lemma

import it.unipi.di.acubelab.wikipediarelatedness.utils.CoreNLP
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa.LuceneIndex
import org.slf4j.LoggerFactory


class LemmaLuceneIndex() extends LuceneIndex() {

  override def getLogger() = LoggerFactory.getLogger(classOf[LemmaLuceneIndex])

  override def wikipediaConcepts(text: String, resultThreshold: Int = 625) : List[Tuple2[Int, Float]] = {
    val lemmaWords = CoreNLP.lemmatize(text) mkString(" ")
    super.wikipediaConcepts(lemmaWords, resultThreshold)
  }
}