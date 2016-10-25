package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa.lemma

import it.unipi.di.acubelab.wikipediarelatedness.utils.CoreNLP
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa.LuceneProcessing
import org.slf4j.LoggerFactory

import scala.util.parsing.json.JSON

class LemmaLuceneProcessing extends LuceneProcessing {
  override def getLogger() = LoggerFactory.getLogger(classOf[LemmaLuceneProcessing])

  override def line2WikiTitleIDBody(line: String): Tuple3[String, Int, String] = {
    val jsonLine = JSON.parseFull(line)

    jsonLine match {
      case Some(jsonObject: Map[String, Any] @unchecked) =>

        val title = jsonObject("wikiTitle").asInstanceOf[String]
        val id = jsonObject("wikiId").asInstanceOf[Double].toInt

        val sentences = jsonObject("sentences").asInstanceOf[List[String]]
        val body = processBody(sentences)

        return (title, id, body.mkString(" "))

      case _ => ;
    }

    throw new IllegalArgumentException("Error while parsing Wikipedia JSON row: %s".format(line))
  }

  def processBody(sentences: List[String]) : List[String] = {
    sentences.par.map {
      sentence =>
        CoreNLP.lemmatize(sentence) mkString(" ")
    }.toList
  }
}
