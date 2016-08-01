package it.unipi.di.acubelab.graphrel.dataset

class WikiRelTask(_src: WikiEntity, _srcWord: String,
                  _dst: WikiEntity, _dstWord: String,
                  _rel: Double) {
  val src = _src
  val srcWord = _srcWord

  val dst = _dst
  val dstWord = _dstWord

  val rel = _rel        // Human Relatedness

  def toList : List[Any] = {
    List(srcWord, src.wikiID, src.wikiTitle,
         dstWord, dst.wikiID, dst.wikiTitle,
         rel)
  }

  override def toString : String = {
    "%s,%d,%s,%s,%d,%s,%.2f".format(srcWord, src.wikiID, src.wikiTitle,
                                    dstWord, dst.wikiID, dst.wikiTitle,
                                    rel)
  }
}
