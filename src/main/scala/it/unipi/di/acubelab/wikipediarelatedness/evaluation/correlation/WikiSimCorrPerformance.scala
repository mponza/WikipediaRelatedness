package it.unipi.di.acubelab.wikipediarelatedness.evaluation.correlation

class WikiSimCorrPerformance(val pearson: Double, val spearman: Double) extends WikiSimPerformance {

  override def toString: String = {
    "Pearson: %1.2f, Spearman: %1.2f".format(pearson, spearman)
  }

  def csvFields() : List[String] = { List ("Pearson", "Spearman") }
  def csvValues() : List[Double] = { List(pearson, spearman)}
}
