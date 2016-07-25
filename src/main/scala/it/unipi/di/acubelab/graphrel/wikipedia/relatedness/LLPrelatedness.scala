package it.unipi.di.acubelab.graphrel.wikipedia.relatedness
import it.unipi.di.acubelab.graphrel.dataset.WikiRelTask
import org.slf4j.LoggerFactory

class LLPrelatedness(options: Map[String, Any]) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[LLPrelatedness])
  val

  override def computeRelatedness(wikiRelTask: WikiRelTask): Double = {
    0.1
  }
}
