package it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim

import it.unipi.di.acubelab.wikipediarelatedness.dataset.{WikiEntity, WikiRelateTask}

class WikiSimTask(src: WikiEntity, dst: WikiEntity, humanRelatedness: Float, val srcWord: String, val dstWord: String)
        extends WikiRelateTask(src: WikiEntity, dst: WikiEntity, humanRelatedness: Float){

  override def toString() : String = "%s,%s,%s".format(srcWord, dstWord, super.toString())
}