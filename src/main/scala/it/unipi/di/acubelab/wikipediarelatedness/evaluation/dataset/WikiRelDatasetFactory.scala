package it.unipi.di.acubelab.wikipediarelatedness.evaluation.dataset

import org.slf4j.LoggerFactory

class WikiRelDatasetFactory {}

object WikiRelDatasetFactory {

  private val logger = LoggerFactory.getLogger(classOf[WikiRelDataset])

  def apply(name: String): WikiRelDataset = name match {

    case "WiRe" =>
      val wire = new WiReDataset("/home/ponza/Developer/WikipediaRelatedness/src/main/resources/datasets/WiRe.csv") // to be fixed
      wire
  }

}
