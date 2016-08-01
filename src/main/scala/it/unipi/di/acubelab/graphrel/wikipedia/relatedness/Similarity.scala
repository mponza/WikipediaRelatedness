package it.unipi.di.acubelab.graphrel.wikipedia.relatedness

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
    val length = math.max(Collections.max(src), Collections.max(dst)) + 1

    // src and dst to one-hot encoded arrays
    val src1Hot = Array.ofDim[Int](length)
    for(i <- 0 until src.size) src1Hot(src.getInt(i)) = 1
    val dst1Hot = Array.ofDim[Int] (length)
    for(i <- 0 until src.size) dst1Hot(dst.getInt(i)) = 1

    val dotProduct = (for((s, d) <- src1Hot zip dst1Hot) yield s * d).sum
    val magSrc = math.sqrt(src1Hot.map(i => i*i).sum)
    val magDst = math.sqrt(dst1Hot.map(i => i*i).sum)

    (dotProduct / (magSrc * magDst) + 1) / 2.toDouble
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
