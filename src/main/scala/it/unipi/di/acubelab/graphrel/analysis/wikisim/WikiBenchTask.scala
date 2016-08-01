package it.unipi.di.acubelab.graphrel.analysis.wikisim

import it.unipi.di.acubelab.graphrel.dataset.{WikiEntity, WikiRelTask}

class WikiBenchTask(_src: WikiEntity, _srcWord: String,
                    _dst: WikiEntity, _dstWord: String,
                    _rel: Double, _computedRel: Double) extends WikiRelTask(_src: WikiEntity, _srcWord: String,
                                                                            _dst: WikiEntity, _dstWord: String,
                                                                            _rel: Double) {
  val computedRel = _computedRel

  override def toList : List[Any] = {
    super.toList :+ computedRel
  }

  override def toString : String = {
    super.toString ++ ",%.2f".format(computedRel)
  }
}
