package it.unipi.di.acubelab.wikipediarelatedness.utils

import edu.stanford.nlp.ling.CoreAnnotations.{SentencesAnnotation, TokensAnnotation}
import edu.stanford.nlp.pipeline.StanfordCoreNLP

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConversions._

object CoreNLP {
  lazy val coreNLP = coreNLPPipeline()

  def coreNLPPipeline() = {
    val props = new java.util.Properties()
    props.put("annotators", "tokenize, ssplit, pos, lemma")
    props.put("tokenize.whitespace", "true")

    new StanfordCoreNLP(props)
  }

  def lemmatize(text: String) : List[String] = {
    // Scala adaptation of: http://stackoverflow.com/questions/1578062/lemmatization-java

    val lemmas = ArrayBuffer.empty[String]

    val document = new edu.stanford.nlp.pipeline.Annotation(text)
    coreNLP.annotate(document)

    val sentences = document.get(classOf[SentencesAnnotation])

    for(sentence <- sentences) {
      val tokens = sentence.get(classOf[TokensAnnotation])

      for(token <- tokens) {
        val lemma = token.lemma()
        lemmas.append(lemma)
      }
    }

    lemmas.toList
  }
}
