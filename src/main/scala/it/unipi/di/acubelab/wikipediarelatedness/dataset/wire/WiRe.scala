package it.unipi.di.acubelab.wikipediarelatedness.dataset.wire

import it.unipi.di.acubelab.wikipediarelatedness.dataset.wire.subwire.{SubWiReNN, SubWiReNS, SubWiReSS}
import it.unipi.di.acubelab.wikipediarelatedness.dataset.{WikiRelateDataset, WikiRelateTask}

class WiRe extends WikiRelateDataset {

  override protected def loadDataset(): Seq[WikiRelateTask] = {
    val dataset = Seq(new SubWiReSS, new SubWiReNS, new SubWiReNN).flatten
    logger.info("WiRe dataset loaded with %d pairs of entities.")

    dataset
  }

  override def toString() = "WiRe"
}

