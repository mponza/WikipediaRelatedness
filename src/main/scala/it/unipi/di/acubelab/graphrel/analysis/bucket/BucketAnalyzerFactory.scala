package it.unipi.di.acubelab.graphrel.analysis.bucket

import it.unipi.di.acubelab.graphrel.dataset.wikisim.WikiSimDataset

object BucketAnalyzerFactory {

  def make(options: Option[Any], relatednessName: String, wikiSimDataset: WikiSimDataset) : BucketAnalyzer = {
    val analyzerName = options.getOrElse("analyzer", "rel").toString

    analyzerName match {
      case "relatedness" | "rel" => new BucketRelAnalyzer(relatednessName, wikiSimDataset)
      case "inRatio" | "in" => new BucketInDegreeAnalyzer(relatednessName, wikiSimDataset)
      case _ => throw new IllegalArgumentException("Analyzer %d does not exist.".format(analyzerName))
    }
  }
}
