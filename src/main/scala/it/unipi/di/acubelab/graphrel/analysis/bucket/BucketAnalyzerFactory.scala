package it.unipi.di.acubelab.graphrel.analysis.bucket

import it.unipi.di.acubelab.graphrel.dataset.wikisim.WikiSimDataset

object BucketAnalyzerFactory {

  def make(analysisName: String, relatednessName: String, wikiSimDataset: WikiSimDataset) : BucketAnalyzer = {
    analysisName match {
      case "Relatedness" => new BucketRelAnalyzer(relatednessName, wikiSimDataset)
      case "InRatio" => new BucketInDegreeAnalyzer(relatednessName, wikiSimDataset)
      case "OutRatio" => new BucketOutDegreeAnalyzer(relatednessName, wikiSimDataset)
      case "SymRatio" => new BucketSymDegreeAnalyzer(relatednessName, wikiSimDataset)
      case _ => throw new IllegalArgumentException("Analyzer %s does not exist.".format(analysisName))
    }
  }
}
