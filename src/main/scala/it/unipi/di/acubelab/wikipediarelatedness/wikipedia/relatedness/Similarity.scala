package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import java.util.Collections

import it.unimi.dsi.fastutil.ints.IntArrayList


object Similarity {

  def computeSimilarity(similarityName: String, src: IntArrayList, dst:IntArrayList) : Double = {
    similarityName match {
      case "hamming" => hammingSimilarity(src, dst)
      case "levenshtein" => levenshteinSimilarity(src, dst)
      case "cosine" => cosineSimilarity(src, dst)
      case  _ => throw new IllegalArgumentException("%s similarity does not exist!".format(similarityName))
    }
  }

  def cosineSimilarity(src: IntArrayList, dst: IntArrayList) : Double = {
    val num = hammingSimilarity(src, dst)
    val den = src.size

    num / den.toDouble max 0.0
  }

  // Code from https://rosettacode.org/wiki/Levenshtein_distance#Scala
  def levenshteinSimilarity(src: IntArrayList, dst: IntArrayList) = {
    val dist = Array.tabulate(src.size + 1, dst.size + 1){(j, i) => if (j == 0) i else if (i == 0) j else 0}

    for(j <- 1 to dst.size; i <- 1 to src.size)
      dist(j)(i) = if(dst.getInt(j - 1) == src.getInt(i - 1)) dist(j - 1)(i - 1)
      else math.min(dist(j - 1)(i) + 1, math.min(dist(j)(i - 1) + 1, dist(j - 1)(i - 1) + 1))

    src.size - dist(dst.size)(src.size)
  }

  def hammingSimilarity(src: IntArrayList, dst: IntArrayList) : Int = {
    var sim = 0
    for(i <- 0 until src.size) {
      sim += (if(src.getInt(i) == dst.getInt(i)) 1 else 0)
    }

    sim
  }
}
