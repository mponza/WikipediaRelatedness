package it.unipi.di.acubelab.graphrel.analysis.bucket

import it.unipi.di.acubelab.graphrel.analysis.bucket.degree.{BucketInDegreeAnalyzer, BucketOutDegreeAnalyzer, BucketSymDegreeAnalyzer}
import it.unipi.di.acubelab.graphrel.dataset.wikisim.WikiSimDataset

object BucketAnalyzerFactory {

  def make(analysisName: String, evalName: String, relatednessName: String, wikiSimDataset: WikiSimDataset)
    : BucketAnalyzer = {

    analysisName match {
      case "Relatedness" => new BucketRelAnalyzer(relatednessName, evalName, wikiSimDataset)

      case "InRatio" => new BucketInDegreeAnalyzer(relatednessName, evalName, wikiSimDataset)
      case "OutRatio" => new BucketOutDegreeAnalyzer(relatednessName, evalName, wikiSimDataset)
      case "SymRatio" => new BucketSymDegreeAnalyzer(relatednessName, evalName, wikiSimDataset)

      case "InDistance" => new BucketInDegreeAnalyzer(relatednessName, evalName, wikiSimDataset)
      case "OutDistance" => new BucketOutDegreeAnalyzer(relatednessName, evalName, wikiSimDataset)
      case "SymDistance" => new BucketSymDegreeAnalyzer(relatednessName, evalName, wikiSimDataset)

      case _ => throw new IllegalArgumentException("Analyzer %s does not exist.".format(analysisName))
    }
  }
}
