package it.unipi.di.acubelab.wikipediarelatedness.utils

import it.unimi.dsi.fastutil.doubles.DoubleArrayList
import it.unimi.dsi.fastutil.floats.FloatArrayList
import it.unimi.dsi.fastutil.objects.ObjectArrayList


object  Similarity {

  /**
    * Computes cosine similarity between two floating point arraylist.
    *
    * @param src
    * @param dst
    * @return
    */
  def cosineSimilarity(src: FloatArrayList,
                       dst: FloatArrayList) : Float = {

    if (src.size() != dst.size()) throw new IllegalArgumentException("src and dst vectors have different sizes.")

    // Computes vectors magnitude
    val srcMagnitude = vectorMagnitude(src)
    val dstMagnitude = vectorMagnitude(dst)
    if(srcMagnitude == 0.0f || dstMagnitude == 0.0f) return 0.0f

    val magnitude = srcMagnitude * dstMagnitude

    // Computes vectors dot product
    var dot = 0.0f
    for(i <- 0 until src.size()) {
      dot += src.getFloat(i) * dst.getFloat(i)
    }

    if (dot == 0.0f) return 0.0f

    dot / magnitude
  }

  def vectorMagnitude(vec: FloatArrayList) : Float = {
    var magnitude = 0f

    for(i <- 0 until vec.size()) {
      magnitude += math.pow(vec.getFloat(i).toDouble, 2f).toFloat
    }

    math.sqrt(magnitude).toFloat
  }


  def cosineSimilarity(src: DoubleArrayList, dst: DoubleArrayList) : Float = {
    val srcFloats = new FloatArrayList(src.toDoubleArray().map(_.toFloat))
    val dstFloats = new FloatArrayList(dst.toDoubleArray().map(_.toFloat))

    cosineSimilarity(srcFloats, dstFloats)
  }


  def cosineSimilarity(src: Seq[Tuple2[Int, Float]], dst: Seq[Tuple2[Int, Float]]) : Float = {
    val srcMagnitude = vectorMagnitude(src)
    val dstMagnitude = vectorMagnitude(dst)
    if (srcMagnitude == 0f || dstMagnitude == 0f) return 0f

    val magnitude = srcMagnitude * dstMagnitude

    var dot = 0f
    var (i, j) = (0, 0)
    while(i < src.length && j < dst.length) {
      val (srcIndex, srcValue) = src(i)
      val (dstIndex, dstValue) = dst(j)

      if (srcValue.isNaN || dstValue.isNaN) {
        throw new IllegalArgumentException("Element in Cosine Similarity is NaN.")
      }

      if(srcIndex == dstIndex) {
        dot = dot + srcValue * dstValue
        i += 1
        j += 1

      } else if (srcIndex < dstIndex) {
        i += 1

      } else {
        j += 1
      }
    }

    if (dot == 0f) return 0f

    dot / magnitude

  }

  def vectorMagnitude(vec: Seq[Tuple2[Int, Float]]) : Float = {
    val powMagnitude = vec.foldLeft(0.0)((magnitude, elemTuple) => magnitude + math.pow(elemTuple._2, 2.0))
    math.sqrt(powMagnitude).toFloat
  }

}
