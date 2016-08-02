package it.unipi.di.acubelab.graphrel.dataset

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
    List(srcWord, src.wikiID, src.wikiTitle,
         dstWord, dst.wikiID, dst.wikiTitle,
         rel, computedRel).filter(_!= null)
  }

  override def toString() : String = {
    val notNaN = List(srcWord, src.wikiID, src.wikiTitle,
          dstWord, dst.wikiID, dst.wikiTitle,
          rel, computedRel).filter{
      case d: Double => !d.isNaN
      case _ => true
    }

    notNaN.map {
      case s: String => s
      case n: Int => n.toString
      case d: Double => "%1.2f".format(d)
    }.mkString(",")
  }

  def make(relatedness: Double): WikiRelTask = {
    new WikiRelTask(src, srcWord,
                    dst, dstWord,
                    rel, relatedness)
  }
}
