package it.unipi.di.acubelab.wikipediarelatedness.utils

object ArithmeticFactory {

  def make(name: String) : List[Float] => Float =
    name match {

      case "avg" | "average" | "mean" => avg
      case "min" => min
      case "max" => max
      case "hmean" | "harmonic" | "harmonicMean" => hmean

  }

  def avg(fs: List[Float]) = if(fs.nonEmpty)  fs.sum / fs.length else 0f
  def min(fs: List[Float]) = if(fs.nonEmpty) fs.min else 0f
  def max(fs: List[Float]) = if(fs.nonEmpty) fs.max else 0f
  def hmean(fs: List[Float]) : Float = if(fs.nonEmpty) fs.length / fs.map(1 / _).sum else 0f
}
