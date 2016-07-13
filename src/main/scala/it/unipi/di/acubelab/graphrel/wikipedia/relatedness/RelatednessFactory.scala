package it.unipi.di.acubelab.graphrel.wikipedia.relatedness

import it.unipi.di.acubelab.graphrel.wikipedia.WikiGraph
import it.unipi.di.acubelab.graphrel.wikipedia.processing.statistics.WikiStats

object RelatednessFactory {
  def make(relateOptions: Option[Any]) : Relatedness = relateOptions match {

    case Some(opts: Map[String, Any]) =>

      val relatednessName = opts("name")
      relatednessName match {

        case "MilneWitten" => new MilneWittenRelatedness(WikiGraph.inGraph, WikiStats.nNodes)

        case _ => throw new IllegalArgumentException("The specified relatedness does not exist %s."
          .format(relatednessName))
      }

    case _ => throw new IllegalArgumentException("Relatedness Options are not valid.")
  }
}
