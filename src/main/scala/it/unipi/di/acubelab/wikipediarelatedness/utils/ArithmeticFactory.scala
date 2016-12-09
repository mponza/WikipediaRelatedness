package it.unipi.di.acubelab.wikipediarelatedness.utils

object ArithmeticFactory {

  def make(name: String) : List[Float] => Float =
    name match {

      case "avg" | "average" | "mean" => avg
      case "min" => min
      case "max" => max
      case "hmean" | "harmonic" | "harmonicMean" => hmean

  }

  def avg(fs: List[Float]) = fs.sum / fs.length
  def min(fs: List[Float]) = fs.min
  def max(fs: List[Float]) = fs.max
  def hmean(fs: List[Float]) : Float = fs.length / fs.map(1 / _).sum
}
