package it.unipi.di.acubelab.wikipediarelatedness.utils

import edu.stanford.nlp.ling.CoreAnnotations.{SentencesAnnotation, TokensAnnotation}
import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import edu.stanford.nlp.util.CoreMap

import scala.collection.mutable.ArrayBuffer


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

    val sentences = document.get(classOf[SentencesAnnotation]).asInstanceOf[List[CoreMap]]

    for(sentence <- sentences) {
      val tokens = sentence.get(classOf[TokensAnnotation]).asInstanceOf[List[CoreLabel]]

      for(token <- tokens) {
        val lemma = token.get(classOf[TokensAnnotation]).asInstanceOf[String]
        lemmas.append(lemma)
      }
    }

    lemmas.toList
  }
}
