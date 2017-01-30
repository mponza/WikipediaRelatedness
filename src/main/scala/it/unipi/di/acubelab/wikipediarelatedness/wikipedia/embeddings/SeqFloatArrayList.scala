package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings

import it.unimi.dsi.fastutil.floats.FloatArrayList

/**
  * Wrapper FloatArrayList as Seq of indexed values.
  *
  */
class SeqFloatArrayList(val array: FloatArrayList) extends Seq[(Int, Float)]{

  def length() = array.size()


  def apply(index: Int) = (index, array.getFloat(index))


  def iterator() = {
    array.toFloatArray().zipWithIndex.map {

      case (value, index) => (index, value)

    }.iterator
  }

}