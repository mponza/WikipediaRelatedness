package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.esa.lemma

import it.unipi.di.acubelab.wikipediarelatedness.utils.CoreNLP
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.esa.LuceneIndex


class LemmaLuceneIndex() extends LuceneIndex() {


  /**
    * Retrieves the lemmatized Wikipedia document from its WikiID.
    *
    * @param wikiID
    * @return
    */
  override def wikipediaBody(wikiID: Int) : String = {
    val rawBody = super.wikipediaBody(wikiID)
    val lemmaBody = CoreNLP.lemmatize(rawBody) mkString(" ")

    lemmaBody
  }
}