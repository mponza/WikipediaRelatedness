package it.unipi.di.acubelab.graphrel.dataset

import java.util.Locale

class WikiRelTask(_src: WikiEntity, _srcWord: String,
                  _dst: WikiEntity, _dstWord: String,
                  _rel: Double, _computedRel: Double = Double.NaN) {
  val src = _src
  val srcWord = _srcWord

  val dst = _dst
  val dstWord = _dstWord

  // Human Relatedness
  val rel = _rel
  // Automatic Relatedness
  var computedRel = _computedRel

  def toList : List[Any] = {
    val notNaNs = List(srcWord, src.wikiID, src.wikiTitle,
      dstWord, dst.wikiID, dst.wikiTitle,
      rel, computedRel).filter {
      case d: Double => !d.isNaN
      case _ => true
    }

    notNaNs.map {
      case d: Double => d.toFloat
      case x => x
    }
  }

  override def toString() : String = {
    toList.map {
      case s: String => s
      case n: Int => n.toString
      case d: Float => "%1.3f".format(d)
    }.mkString(",")
  }

  def toCSVString() : String = {
    toList.map {
      case s: String => if(s.contains(',')) "\"%s\"".format(s) else s
      case n: Int => n.toString
      case d: Double => "%1.3f".formatLocal(Locale.US, d)
    }.mkString(",")
  }

  def make(relatedness: Double): WikiRelTask = {
    new WikiRelTask(src, srcWord,
                    dst, dstWord,
                    rel, relatedness)
  }

  def wikiTitleString() : String = {
    "%s and %s".format(src.wikiTitle, dst.wikiTitle)
  }
}
